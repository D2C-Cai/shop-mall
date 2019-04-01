package com.d2c.shop.customer.api.handler.impl;

import com.d2c.shop.customer.api.handler.OrderHandler;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.member.model.MemberCouponDO;
import com.d2c.shop.service.modules.member.query.MemberCouponQuery;
import com.d2c.shop.service.modules.member.service.MemberCouponService;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.product.model.CouponDO;
import com.d2c.shop.service.modules.product.model.CouponDO.TypeEnum;
import com.d2c.shop.service.modules.product.model.CouponProductDO;
import com.d2c.shop.service.modules.product.service.CouponProductService;
import com.d2c.shop.service.modules.product.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cai
 */
@Component
public class OrderCouponHandler implements OrderHandler {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponProductService couponProductService;
    @Autowired
    private MemberCouponService memberCouponService;

    @Override
    public void operator(OrderDO order, Object... conditions) {
        if (order.getCouponList() == null || order.getCouponList().size() == 0) return;
        MemberCouponDO memberCoupon = order.getCouponList().get(0);
        if (!memberCoupon.available()) return;
        // 获取去重后的优惠券模板
        CouponDO coupon = couponService.getById(memberCoupon.getCouponId());
        List<CouponDO> couponList = new ArrayList<>();
        couponList.add(coupon);
        // 辨别得到符合条件（商品关系，门槛金额）的优惠券模板
        Map<Long, CouponDO> availableMap = this.distinguish(order, couponList);
        if (availableMap.get(coupon.getId()) != null) {
            order.setCouponId(memberCoupon.getId());
            order.setCouponAmount(coupon.getAmount());
            order.setPayAmount(order.getProductAmount().subtract(coupon.getAmount()));
            // 加权拆分优惠券金额
            this.split(order, coupon);
        }
    }

    // 获取用户可使用的优惠券
    public List<MemberCouponDO> availableCoupon(final OrderDO order, Long shopId, Long memberId) {
        MemberCouponQuery query = new MemberCouponQuery();
        Date now = new Date();
        query.setShopId(shopId);
        query.setMemberId(memberId);
        query.setStatus(1);
        query.setServiceStartDateR(now);
        query.setServiceEndDateL(now);
        List<MemberCouponDO> list = memberCouponService.list(QueryUtil.buildWrapper(query));
        // 获取去重后的优惠券模板
        Set<Long> couponIds = new HashSet<>();
        list.forEach(item -> couponIds.add(item.getCouponId()));
        if (couponIds.size() == 0) return new ArrayList<>();
        List<CouponDO> couponList = (List<CouponDO>) couponService.listByIds(couponIds);
        // 辨别得到符合条件（商品关系，门槛金额）的优惠券模板
        Map<Long, CouponDO> availableMap = this.distinguish(order, couponList);
        // 返回此订单下用户可选优惠券
        List<MemberCouponDO> survive = new ArrayList<>();
        for (MemberCouponDO memberCoupon : list) {
            CouponDO available = availableMap.get(memberCoupon.getCouponId());
            if (available != null) {
                memberCoupon.setName(available.getName());
                memberCoupon.setNeedAmount(available.getNeedAmount());
                memberCoupon.setAmount(available.getAmount());
                memberCoupon.setRemark(available.getRemark());
                survive.add(memberCoupon);
            }
        }
        return survive;
    }

    // 辨别得到符合条件（商品关系，门槛金额）的优惠券模板
    private Map<Long, CouponDO> distinguish(final OrderDO order, List<CouponDO> couponList) {
        // 构建<优惠券模板ID，优惠券模板>映射，方便后续使用
        Map<Long, CouponDO> couponMap = new ConcurrentHashMap<>();
        couponList.forEach(item -> couponMap.put(item.getId(), item));
        // 构建<商品ID，订单明细>映射，方便后续使用
        List<OrderItemDO> orderItemList = order.getOrderItemList();
        Map<Long, OrderItemDO> orderItemMap = new ConcurrentHashMap<>();
        // 全部订单明细的商品总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 订单明细的商品ID集合
        List<Long> productIds = new ArrayList<>();
        for (OrderItemDO orderItem : orderItemList) {
            orderItemMap.put(orderItem.getProductId(), orderItem);
            totalAmount = totalAmount.add(orderItem.getPayAmount());
            productIds.add(orderItem.getProductId());
        }
        // 待选优惠券模板类型为（指定商品可用）的ID集合
        List<Long> includeIds = new ArrayList<>();
        // 待选优惠券模板类型为（指定商品不可用）的ID集合
        List<Long> excludeIds = new ArrayList<>();
        List<CouponDO> available = new ArrayList<>();
        for (CouponDO coupon : couponList) {
            if (coupon.getStatus() != 1) continue;
            switch (TypeEnum.getEnum(coupon.getType())) {
                case ALL:
                    // 全部商品可用，只须判断全部订单明细的商品总金额
                    if (totalAmount.compareTo(coupon.getNeedAmount()) >= 0) {
                        available.add(coupon);
                    }
                    break;
                case INCLUDE:
                    // 指定商品可用
                    includeIds.add(coupon.getId());
                    break;
                case EXCLUDE:
                    // 指定商品不可用
                    excludeIds.add(coupon.getId());
                    break;
                default:
                    break;
            }
        }
        if (includeIds.size() > 0) {
            // 指定商品可用
            List<CouponProductDO> include = couponProductService.findQualified(includeIds, productIds, TypeEnum.INCLUDE.getCode());
            // 构建<优惠券模板ID，满足条件的订单明细商品总金额>映射，分成N个购物袋
            Map<Long, BigDecimal> bag = new ConcurrentHashMap<>();
            for (CouponProductDO item : include) {
                BigDecimal itemAmount = BigDecimal.ZERO;
                if (bag.get(item.getCouponId()) == null) {
                    itemAmount = orderItemMap.get(item.getProductId()).getPayAmount();
                } else {
                    itemAmount = bag.get(item.getCouponId());
                    itemAmount = itemAmount.add(orderItemMap.get(item.getProductId()).getPayAmount());
                }
                bag.put(item.getCouponId(), itemAmount);
            }
            // 每个购物袋判断满足条件的订单明细商品总金额
            for (Long couponId : bag.keySet()) {
                CouponDO coupon = couponMap.get(couponId);
                if (bag.get(couponId).compareTo(coupon.getNeedAmount()) >= 0) {
                    available.add(coupon);
                }
            }
        }
        if (excludeIds.size() > 0) {
            // 指定商品不可用
            List<CouponProductDO> exclude = couponProductService.findQualified(excludeIds, productIds, TypeEnum.EXCLUDE.getCode());
            List<Long> excludes = new ArrayList<>();
            exclude.forEach(item -> excludes.add(item.getCouponId()));
            // 除去不符合条件的优惠券模板
            excludeIds.removeAll(excludes);
            for (Long couponId : excludeIds) {
                CouponDO coupon = couponMap.get(couponId);
                // 满足条件的优惠券模板，只须判断全部订单明细的商品总金额
                if (totalAmount.compareTo(coupon.getNeedAmount()) >= 0) {
                    available.add(coupon);
                }
            }
        }
        Map<Long, CouponDO> availableMap = new ConcurrentHashMap<>();
        available.forEach(item -> availableMap.put(item.getId(), item));
        return availableMap;
    }

    // 加权拆分优惠券金额
    private void split(OrderDO order, CouponDO coupon) {
        List<OrderItemDO> orderItemList = order.getOrderItemList();
        if (orderItemList.size() == 1) {
            OrderItemDO validItem = orderItemList.get(0);
            validItem.setCouponWeightingAmount(order.getCouponAmount());
            validItem.setPayAmount(validItem.getPayAmount().subtract(validItem.getCouponWeightingAmount()));
            return;
        }
        // 构建<商品ID，订单明细>映射，方便后续使用
        Map<Long, OrderItemDO> orderItemMap = new ConcurrentHashMap<>();
        // 全部订单明细的商品总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 订单明细的商品ID集合
        List<Long> productIds = new ArrayList<>();
        for (OrderItemDO orderItem : orderItemList) {
            orderItemMap.put(orderItem.getProductId(), orderItem);
            totalAmount = totalAmount.add(orderItem.getPayAmount());
            productIds.add(orderItem.getProductId());
        }
        Map<Long, OrderItemDO> availableMap = new ConcurrentHashMap<>();
        switch (TypeEnum.getEnum(coupon.getType())) {
            case ALL:
                // 全部商品可用
                availableMap.putAll(orderItemMap);
                break;
            case EXCLUDE:
                // 指定商品不可用
                availableMap.putAll(orderItemMap);
                break;
            case INCLUDE:
                // 指定商品可用
                totalAmount = BigDecimal.ZERO;
                List<CouponProductDO> include = couponProductService.findQualified(Arrays.asList(new Long[]{coupon.getId()}), productIds, TypeEnum.INCLUDE.getCode());
                for (CouponProductDO item : include) {
                    availableMap.put(item.getProductId(), orderItemMap.get(item.getProductId()));
                    totalAmount = totalAmount.add(orderItemMap.get(item.getProductId()).getPayAmount());
                }
                break;
            default:
                break;
        }
        // 加权拆分优惠券金额
        BigDecimal splitAmount = BigDecimal.ZERO;
        BigDecimal couponAmount = order.getCouponAmount();
        Long[] keyArray = availableMap.keySet().toArray(new Long[0]);
        for (int i = 0; i < keyArray.length; i++) {
            OrderItemDO validItem = availableMap.get(keyArray[i]);
            if (i == keyArray.length - 1) {
                // 最后一条记录用减法，防止小数点误差
                validItem.setCouponWeightingAmount(couponAmount.subtract(splitAmount));
                validItem.setPayAmount(validItem.getPayAmount().subtract(validItem.getCouponWeightingAmount()));
            } else {
                BigDecimal weightingAmount = validItem.getPayAmount().multiply(couponAmount).divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP);
                validItem.setCouponWeightingAmount(weightingAmount);
                validItem.setPayAmount(validItem.getPayAmount().subtract(validItem.getCouponWeightingAmount()));
                splitAmount = splitAmount.add(weightingAmount);
            }
        }
    }

}
