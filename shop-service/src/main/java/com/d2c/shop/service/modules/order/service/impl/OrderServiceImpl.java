package com.d2c.shop.service.modules.order.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.common.utils.ExecutorUtil;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopFlowDO;
import com.d2c.shop.service.modules.core.service.ShopFlowService;
import com.d2c.shop.service.modules.member.model.MemberCouponDO;
import com.d2c.shop.service.modules.member.model.MemberShopDO;
import com.d2c.shop.service.modules.member.query.MemberShopQuery;
import com.d2c.shop.service.modules.member.service.MemberCouponService;
import com.d2c.shop.service.modules.member.service.MemberService;
import com.d2c.shop.service.modules.member.service.MemberShopService;
import com.d2c.shop.service.modules.order.mapper.OrderMapper;
import com.d2c.shop.service.modules.order.model.CrowdGroupDO;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.order.model.PaymentDO;
import com.d2c.shop.service.modules.order.query.OrderItemQuery;
import com.d2c.shop.service.modules.order.query.OrderQuery;
import com.d2c.shop.service.modules.order.query.PaymentQuery;
import com.d2c.shop.service.modules.order.service.CrowdGroupService;
import com.d2c.shop.service.modules.order.service.OrderItemService;
import com.d2c.shop.service.modules.order.service.OrderService;
import com.d2c.shop.service.modules.order.service.PaymentService;
import com.d2c.shop.service.modules.product.model.CouponDO;
import com.d2c.shop.service.modules.product.model.ProductDO;
import com.d2c.shop.service.modules.product.service.CouponService;
import com.d2c.shop.service.modules.product.service.ProductService;
import com.d2c.shop.service.modules.product.service.ProductSkuService;
import com.d2c.shop.service.rabbitmq.sender.OrderDelayedSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author BaiCai
 */
@Service
public class OrderServiceImpl extends BaseService<OrderMapper, OrderDO> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSkuService productSkuService;
    @Autowired
    private CrowdGroupService crowdGroupService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private MemberCouponService memberCouponService;
    @Autowired
    private MemberShopService memberShopService;
    @Autowired
    private ShopFlowService shopFlowService;
    @Autowired
    private OrderDelayedSender orderDelayedSender;

    @Override
    @Transactional
    public boolean save(OrderDO entity) {
        boolean success = super.save(entity);
        // 发送延迟消息
        ExecutorUtil.fixedPool.submit(() -> {
                    orderDelayedSender.send(entity.getSn(), entity.getExpireMinute() * 60L);
                }
        );
        return success;
    }

    @Override
    @Transactional
    public OrderDO doCreate(OrderDO order) {
        List<OrderItemDO> orderItemList = order.getOrderItemList();
        if (orderItemList.size() == 0) {
            throw new ApiException("订单明细不能为空");
        }
        // 拼团开团/参团
        CrowdGroupDO crowdGroup = order.getCrowdGroup();
        if (crowdGroup != null) {
            if (order.getCrowdId().intValue() == 0) {
                crowdGroupService.save(crowdGroup);
                order.setCrowdId(crowdGroup.getId());
            } else {
                int success = crowdGroupService.doAttend(crowdGroup.getId(), crowdGroup.getAvatars());
                if (success == 0) {
                    throw new ApiException(crowdGroup.getId() + "的团组已满员");
                }
            }
        }
        // 创建订单
        this.save(order);
        for (OrderItemDO orderItem : orderItemList) {
            // 扣减库存
            int success = productSkuService.doDeductStock(orderItem.getProductSkuId(), orderItem.getProductId(), orderItem.getQuantity());
            if (success == 0) {
                throw new ApiException(orderItem.getProductSkuId() + "的SKU库存不足");
            }
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getSn());
            orderItem.setType(order.getType());
            orderItem.setShopId(order.getShopId());
            orderItem.setShopName(order.getShopName());
            orderItem.setCrowdId(order.getCrowdId());
            orderItemService.save(orderItem);
        }
        // 优惠券核销
        if (order.getCouponId() != null) {
            MemberCouponDO memberCoupon = new MemberCouponDO();
            memberCoupon.setId(order.getCouponId());
            memberCoupon.setStatus(0);
            memberCouponService.updateById(memberCoupon);
        }
        return order;
    }

    @Override
    @Transactional
    public boolean doPayment(OrderDO order, PaymentDO.PaymentTypeEnum paymentType, String paymentSn, String mchId) {
        boolean success = true;
        // 支付订单
        OrderDO o = new OrderDO();
        o.setId(order.getId());
        o.setStatus(OrderDO.StatusEnum.PAID.name());
        o.setPaymentType(paymentType.name());
        o.setPaymentSn(paymentSn);
        success &= this.updateById(o);
        // 支付订单明细
        OrderItemDO oi = new OrderItemDO();
        if (order.getCrowdId() != null && order.getCrowdId() > 0) {
            // 拼团明细状态-已支付
            oi.setStatus(OrderItemDO.StatusEnum.PAID.name());
        } else {
            // 普通明细状态-待发货
            oi.setStatus(OrderItemDO.StatusEnum.WAIT_DELIVER.name());
        }
        oi.setPaymentType(paymentType.name());
        oi.setPaymentSn(paymentSn);
        OrderItemQuery oiq = new OrderItemQuery();
        oiq.setOrderSn(new String[]{order.getSn()});
        success &= orderItemService.update(oi, QueryUtil.buildWrapper(oiq));
        // 支付单核销
        PaymentDO p = new PaymentDO();
        p.setStatus(1);
        p.setPaymentSn(paymentSn);
        if (paymentType.equals(PaymentDO.PaymentTypeEnum.ALI_PAY)) {
            p.setPrepayId(paymentSn);
            p.setMchId(mchId);
        }
        PaymentQuery pq = new PaymentQuery();
        pq.setOrderSn(order.getSn());
        success &= paymentService.update(p, QueryUtil.buildWrapper(pq));
        // 拼团业务处理
        if (order.getCrowdId() != null && order.getCrowdId() > 0) {
            CrowdGroupDO crowdGroup = crowdGroupService.getById(order.getCrowdId());
            CrowdGroupDO entity = new CrowdGroupDO();
            entity.setId(crowdGroup.getId());
            entity.setPaidNum(crowdGroup.getPaidNum() + 1);
            if (crowdGroup.getCrowdNum() == crowdGroup.getPaidNum() + 1) {
                entity.setStatus(1);
                OrderItemDO noi = new OrderItemDO();
                if (crowdGroup.getVirtual() == 1) {
                    // 虚拟商品明细状态-已发货
                    noi.setStatus(OrderItemDO.StatusEnum.DELIVERED.name());
                    // 发放拼团优惠券
                    this.sendCrowdCoupon(crowdGroup);
                } else {
                    // 普通商品明细状态-待发货
                    noi.setStatus(OrderItemDO.StatusEnum.WAIT_DELIVER.name());
                }
                OrderItemQuery noiq = new OrderItemQuery();
                noiq.setCrowdId(order.getCrowdId());
                orderItemService.update(noi, QueryUtil.buildWrapper(noiq));
            }
            crowdGroupService.updateById(entity);
        }
        ExecutorUtil.fixedPool.submit(() -> {
                    // 最后统计消费
                    this.statisticConsumption(order, paymentType, paymentSn);
                }
        );
        return success;
    }

    // 发放拼团优惠券
    private void sendCrowdCoupon(CrowdGroupDO crowdGroup) {
        ProductDO product = productService.getById(crowdGroup.getProductId());
        CouponDO coupon = couponService.getById(product.getCouponId());
        if (coupon != null) {
            OrderItemQuery noiq = new OrderItemQuery();
            noiq.setCrowdId(crowdGroup.getId());
            noiq.setStatus(new String[]{OrderItemDO.StatusEnum.PAID.name()});
            List<OrderItemDO> oiList = orderItemService.list(QueryUtil.buildWrapper(noiq));
            for (OrderItemDO item : oiList) {
                MemberCouponDO memberCoupon = new MemberCouponDO();
                memberCoupon.setMemberId(item.getMemberId());
                memberCoupon.setCouponId(coupon.getId());
                memberCoupon.setShopId(item.getShopId());
                memberCoupon.setShopName(item.getShopName());
                memberCoupon.setStatus(1);
                Date serviceStartDate = coupon.getServiceStartDate();
                Date serviceEndDate = coupon.getServiceEndDate();
                if (coupon.getServiceSustain() != null && coupon.getServiceSustain() > 0) {
                    serviceStartDate = new Date();
                    serviceEndDate = DateUtil.offsetHour(serviceStartDate, coupon.getServiceSustain());
                }
                memberCoupon.setServiceStartDate(serviceStartDate);
                memberCoupon.setServiceEndDate(serviceEndDate);
                memberCouponService.doSend(memberCoupon);
            }
        }
    }

    // 最后统计消费
    private void statisticConsumption(OrderDO order, PaymentDO.PaymentTypeEnum paymentType, String paymentSn) {
        // 会员消费统计
        memberService.doConsume(order.getMemberId(), order.getMemberAccount(), order.getPayAmount());
        // 会员归属门店
        MemberShopDO ms = new MemberShopDO();
        ms.setShopId(order.getShopId());
        ms.setMemberId(order.getMemberId());
        MemberShopQuery msq = new MemberShopQuery();
        msq.setShopId(order.getShopId());
        msq.setMemberId(order.getMemberId());
        memberShopService.remove(QueryUtil.buildWrapper(msq));
        memberShopService.save(ms);
        // 门店订单收入
        ShopFlowDO sf = new ShopFlowDO();
        sf.setStatus(1);
        sf.setType(ShopFlowDO.TypeEnum.ORDER.name());
        sf.setShopId(order.getShopId());
        sf.setOrderSn(order.getSn());
        sf.setPaymentType(paymentType.name());
        sf.setPaymentSn(paymentSn);
        sf.setAmount(order.getPayAmount());
        shopFlowService.doFlowing(sf);
    }

    @Override
    @Transactional
    public boolean doClose(OrderDO order) {
        boolean success = true;
        // 关闭订单
        OrderDO o = new OrderDO();
        o.setId(order.getId());
        o.setStatus(OrderDO.StatusEnum.CLOSED.name());
        success &= this.updateById(o);
        // 关闭订单明细
        OrderItemDO oi = new OrderItemDO();
        oi.setStatus(OrderItemDO.StatusEnum.CLOSED.name());
        OrderItemQuery oiq = new OrderItemQuery();
        oiq.setOrderSn(new String[]{order.getSn()});
        success &= orderItemService.update(oi, QueryUtil.buildWrapper(oiq));
        // 优惠券返还
        if (order.getCouponId() != null) {
            MemberCouponDO memberCoupon = new MemberCouponDO();
            memberCoupon.setId(order.getCouponId());
            memberCoupon.setStatus(1);
            memberCouponService.updateById(memberCoupon);
        }
        // 拼团业务处理
        if (order.getCrowdId() != null && order.getCrowdId() > 0) {
            CrowdGroupDO crowdGroup = crowdGroupService.getById(order.getCrowdId());
            crowdGroupService.doCancel(order.getCrowdId(), crowdGroup.popAvatars(order.getMemberId()));
        }
        List<OrderItemDO> orderItemList = order.getOrderItemList();
        for (OrderItemDO orderItem : orderItemList) {
            // 返还库存
            productSkuService.doReturnStock(orderItem.getProductSkuId(), orderItem.getProductId(), orderItem.getQuantity());
        }
        return success;
    }

    @Override
    @Transactional
    public boolean doDelete(String orderSn) {
        boolean success = true;
        OrderQuery oq = new OrderQuery();
        oq.setSn(orderSn);
        success &= this.remove(QueryUtil.buildWrapper(oq));
        OrderItemQuery oiq = new OrderItemQuery();
        oiq.setOrderSn(new String[]{orderSn});
        success &= orderItemService.remove(QueryUtil.buildWrapper(oiq));
        return success;
    }

    @Override
    public Map<String, Object> countDaily(OrderQuery query) {
        return orderMapper.countDaily(query);
    }

}
