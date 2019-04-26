package com.d2c.shop.customer.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.customer.api.base.BaseController;
import com.d2c.shop.customer.api.handler.OrderHandler;
import com.d2c.shop.customer.api.handler.impl.OrderCouponHandler;
import com.d2c.shop.customer.api.handler.impl.OrderCrowdHandler;
import com.d2c.shop.customer.api.handler.impl.OrderPromotionHandler;
import com.d2c.shop.customer.api.support.OrderRequestBean;
import com.d2c.shop.customer.sdk.pay.alipay.AliPayConfig;
import com.d2c.shop.customer.sdk.pay.wxpay.WXPay;
import com.d2c.shop.customer.sdk.pay.wxpay.config.WxPayConfig;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.PrefixConstant;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.common.utils.ReflectUtil;
import com.d2c.shop.service.common.utils.RequestUtil;
import com.d2c.shop.service.modules.core.model.ShopDO;
import com.d2c.shop.service.modules.core.service.ShopService;
import com.d2c.shop.service.modules.member.model.AddressDO;
import com.d2c.shop.service.modules.member.model.MemberCouponDO;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.member.model.OauthDO;
import com.d2c.shop.service.modules.member.service.AddressService;
import com.d2c.shop.service.modules.member.service.MemberCouponService;
import com.d2c.shop.service.modules.order.model.*;
import com.d2c.shop.service.modules.order.query.OrderItemQuery;
import com.d2c.shop.service.modules.order.query.OrderQuery;
import com.d2c.shop.service.modules.order.query.PaymentQuery;
import com.d2c.shop.service.modules.order.service.*;
import com.d2c.shop.service.modules.product.model.ProductDO;
import com.d2c.shop.service.modules.product.model.ProductSkuDO;
import com.d2c.shop.service.modules.product.service.ProductService;
import com.d2c.shop.service.modules.product.service.ProductSkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Cai
 */
@Api(description = "订单业务")
@RestController
@RequestMapping("/c_api/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSkuService productSkuService;
    @Autowired
    private CrowdGroupService crowdGroupService;
    @Autowired
    private MemberCouponService memberCouponService;
    @Autowired
    private OrderCrowdHandler orderCrowdHandler;
    @Autowired
    private OrderCouponHandler orderCouponHandler;
    @Autowired
    private OrderPromotionHandler orderPromotionHandler;
    @Autowired
    private OauthController oauthController;
    @Autowired
    private WxPayConfig wxPayConfig;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "立即结算")
    @RequestMapping(value = "/settle", method = RequestMethod.POST)
    public R<OrderDO> settle(@RequestBody OrderRequestBean orderRequest) {
        // 参数校验
        List<Long> cartIds = orderRequest.getCartIds();
        Long skuId = orderRequest.getSkuId();
        Integer quantity = orderRequest.getQuantity();
        Long crowdId = orderRequest.getCrowdId();
        if (cartIds == null && skuId == null && quantity == null) {
            throw new ApiException(ResultCode.REQUEST_PARAM_NULL);
        }
        // 登录用户
        MemberDO member = loginMemberHolder.getLoginMember();
        // 构建订单
        OrderDO order = new OrderDO();
        order.setMemberId(member.getId());
        order.setMemberAccount(member.getAccount());
        order.setProductAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setPayAmount(BigDecimal.ZERO);
        // 构建订单明细
        List<OrderItemDO> orderItemList = this.buildOrderItemList(cartIds, skuId, quantity, member);
        order.setOrderItemList(orderItemList);
        // 拼团业务
        if (crowdId != null) {
            if (crowdId.intValue() == 0) {
                order.setCrowdId(crowdId);
            } else {
                CrowdGroupDO crowdGroup = crowdGroupService.getById(crowdId);
                Asserts.notNull("该团组不存在", crowdGroup);
                Asserts.eq(crowdGroup.getStatus(), 0, "该团组状态异常");
                Asserts.eq(crowdGroup.available(), true, "该团组满员或已过期");
                order.setCrowdId(crowdId);
                order.setCrowdGroup(crowdGroup);
            }
        }
        // 按照顺序，处理订单促销
        orderCrowdHandler.operator(order, member);
        orderPromotionHandler.operator(order);
        order.setCouponList(orderCouponHandler.availableCoupon(order, orderItemList.get(0).getShopId(), member.getId()));
        return Response.restResult(order, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public R<OrderDO> create(@RequestBody OrderRequestBean orderRequest) {
        // 参数校验
        List<Long> cartIds = orderRequest.getCartIds();
        Long skuId = orderRequest.getSkuId();
        Integer quantity = orderRequest.getQuantity();
        Long addressId = orderRequest.getAddressId();
        Long crowdId = orderRequest.getCrowdId();
        Long couponId = orderRequest.getCouponId();
        if (cartIds == null && skuId == null && quantity == null) {
            throw new ApiException(ResultCode.REQUEST_PARAM_NULL);
        }
        // 登录用户
        MemberDO member = loginMemberHolder.getLoginMember();
        // 防止重复提交
        try {
            Object doing = redisTemplate.opsForValue().get("C_CREATE_ORDER::" + member.getAccount());
            Asserts.isNull("您尚有正在处理中的订单，请勿重复操作", doing);
            redisTemplate.opsForValue().set("C_CREATE_ORDER::" + member.getAccount(), 1, 1, TimeUnit.MINUTES);
            // 收货地址
            AddressDO address = addressService.getById(addressId);
            Asserts.notNull("收货地址不能为空", address);
            Asserts.eq(address.getMemberId(), member.getId(), "收货地址不属于本人");
            // 构建订单
            Snowflake snowFlake = new Snowflake(1, 1);
            OrderDO order = new OrderDO();
            BeanUtil.copyProperties(address, order, ReflectUtil.clearPublicFields());
            order.setSn(PrefixConstant.ORDER_PREFIX + String.valueOf(snowFlake.nextId()));
            order.setType(OrderDO.TypeEnum.NORMAL.name());
            order.setStatus(OrderDO.StatusEnum.WAIT_PAY.name());
            order.setProductAmount(BigDecimal.ZERO);
            order.setCouponAmount(BigDecimal.ZERO);
            order.setPayAmount(BigDecimal.ZERO);
            // 构建订单明细
            List<OrderItemDO> orderItemList = this.buildOrderItemList(cartIds, skuId, quantity, member);
            order.setOrderItemList(orderItemList);
            // 下单店铺
            ShopDO shop = shopService.getById(orderItemList.get(0).getShopId());
            Asserts.notNull("店铺不能为空", shop);
            order.setShopId(shop.getId());
            order.setShopName(shop.getName());
            // 拼团业务
            if (crowdId != null) {
                if (crowdId.intValue() == 0) {
                    order.setCrowdId(crowdId);
                } else {
                    CrowdGroupDO crowdGroup = crowdGroupService.getById(crowdId);
                    Asserts.notNull("该团组不存在", crowdGroup);
                    Asserts.eq(crowdGroup.getStatus(), 0, "该团组状态异常");
                    Asserts.eq(crowdGroup.available(), true, "该团组满员或已过期");
                    order.setCrowdId(crowdId);
                    order.setCrowdGroup(crowdGroup);
                }
            }
            // 优惠券业务
            if (couponId != null) {
                MemberCouponDO memberCoupon = memberCouponService.getById(couponId);
                Asserts.notNull("该优惠券不存在", memberCoupon);
                Asserts.eq(memberCoupon.getMemberId(), member.getId(), "该优惠券不属于本人");
                Asserts.eq(memberCoupon.available(), true, "该优惠券不可用");
                order.getCouponList().add(memberCoupon);
            }
            // 按照顺序，处理订单促销
            List<OrderHandler> handlers = new ArrayList<>();
            handlers.add(orderCrowdHandler);
            handlers.add(orderPromotionHandler);
            handlers.add(orderCouponHandler);
            for (OrderHandler handler : handlers) {
                handler.operator(order, member);
            }
            // 创建订单
            order = orderService.doCreate(order);
            // 清空购物车
            if (cartIds != null && cartIds.size() > 0) {
                cartItemService.removeByIds(cartIds);
            }
            return Response.restResult(order, ResultCode.SUCCESS);
        } finally {
            redisTemplate.delete("C_CREATE_ORDER::" + member.getAccount());
        }
    }

    // 构建订单明细
    private List<OrderItemDO> buildOrderItemList(List<Long> cartIds, Long skuId, Integer quantity, MemberDO member) {
        List<OrderItemDO> orderItemList = new ArrayList<>();
        if (cartIds != null && cartIds.size() > 0) {
            // 从购物车结算
            return this.buildCartOrderItems(cartIds);
        } else if (skuId != null && quantity != null) {
            // 从立即购买结算
            return this.buildBuyNowOrderItems(skuId, quantity, member);
        } else {
            throw new ApiException(ResultCode.REQUEST_PARAM_NULL);
        }
    }

    // 从购物车结算
    private List<OrderItemDO> buildCartOrderItems(List<Long> cartIds) {
        List<OrderItemDO> orderItemList = new ArrayList<>();
        List<CartItemDO> list = (List<CartItemDO>) cartItemService.listByIds(cartIds);
        List<Long> skuIds = new ArrayList<>();
        Map<Long, CartItemDO> map = new ConcurrentHashMap<>();
        for (CartItemDO cartItem : list) {
            skuIds.add(cartItem.getProductSkuId());
            map.put(cartItem.getProductSkuId(), cartItem);
        }
        Asserts.gt(skuIds.size(), 0, "购物车数据异常");
        List<ProductSkuDO> skuList = (List<ProductSkuDO>) productSkuService.listByIds(skuIds);
        for (ProductSkuDO sku : skuList) {
            if (map.get(sku.getId()) != null) {
                Asserts.ge(sku.getStock(), map.get(sku.getId()).getQuantity(), sku.getId() + "的SKU库存不足");
                OrderItemDO orderItem = this.initOrderItem(map, sku);
                this.buildOrderItem(sku, orderItem);
                orderItemList.add(orderItem);
            }
        }
        return orderItemList;
    }

    // 从立即购买结算
    private List<OrderItemDO> buildBuyNowOrderItems(Long skuId, Integer quantity, MemberDO member) {
        List<OrderItemDO> orderItemList = new ArrayList<>();
        Asserts.gt(quantity, 0, "数量必须大于0");
        ProductSkuDO sku = productSkuService.getById(skuId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, sku);
        Asserts.ge(sku.getStock(), quantity, sku.getId() + "的SKU库存不足");
        ProductDO product = productService.getById(sku.getProductId());
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, product);
        OrderItemDO orderItem = this.initOrderItem(quantity, member, sku, product);
        this.buildOrderItem(sku, orderItem);
        orderItem.setProduct(product);
        orderItemList.add(orderItem);
        return orderItemList;
    }

    private OrderItemDO initOrderItem(Map<Long, CartItemDO> map, ProductSkuDO sku) {
        OrderItemDO orderItem = new OrderItemDO();
        CartItemDO cartItem = map.get(sku.getId());
        BeanUtil.copyProperties(cartItem, orderItem, ReflectUtil.clearPublicFields());
        return orderItem;
    }

    private OrderItemDO initOrderItem(Integer quantity, MemberDO member, ProductSkuDO sku, ProductDO product) {
        OrderItemDO orderItem = new OrderItemDO();
        orderItem.setShopId(sku.getShopId());
        orderItem.setMemberId(member.getId());
        orderItem.setMemberAccount(member.getAccount());
        orderItem.setProductId(sku.getProductId());
        orderItem.setProductSkuId(sku.getId());
        orderItem.setQuantity(quantity);
        orderItem.setStandard(sku.getStandard());
        orderItem.setProductName(product.getName());
        orderItem.setProductPic(product.getFirstPic());
        return orderItem;
    }

    private void buildOrderItem(ProductSkuDO sku, OrderItemDO orderItem) {
        orderItem.setType(OrderDO.TypeEnum.NORMAL.name());
        orderItem.setStatus(OrderItemDO.StatusEnum.WAIT_PAY.name());
        orderItem.setVirtual(sku.getVirtual());
        orderItem.setProductPrice(sku.getSellPrice());
        orderItem.setRealPrice(sku.getSellPrice());
        orderItem.setPayAmount(BigDecimal.ZERO);
        orderItem.setCouponWeightingAmount(BigDecimal.ZERO);
    }

    @ApiOperation(value = "支付订单")
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public R pay(Long id, String paymentType, String tradeType) throws Exception {
        OrderDO order = orderService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, order);
        PaymentDO payment = new PaymentDO();
        payment.setStatus(0);
        payment.setOrderSn(order.getSn());
        payment.setAmount(order.getPayAmount());
        PaymentDO.PaymentTypeEnum paymentTypeEnum = PaymentDO.PaymentTypeEnum.valueOf(paymentType);
        String responseData = "";
        switch (paymentTypeEnum) {
            case WX_PAY:
                responseData = this.wxPay(order, payment, tradeType);
                break;
            case ALI_PAY:
                responseData = this.aliPay(order, payment);
                break;
            default:
                break;
        }
        PaymentQuery query = new PaymentQuery();
        query.setOrderSn(order.getSn());
        PaymentDO old = paymentService.getOne(QueryUtil.buildWrapper(query));
        if (old == null) {
            paymentService.save(payment);
        } else {
            payment.setId(old.getId());
            paymentService.updateById(payment);
        }
        return Response.restResult(responseData, ResultCode.SUCCESS);
    }

    private String wxPay(OrderDO order, PaymentDO payment, String tradeType) throws Exception {
        WXPay wxPay = new WXPay(wxPayConfig, WxPayConfig.NOTIFY_URL);
        Map<String, String> reqData = new HashMap<>();
        reqData.put("body", String.valueOf(order.getId()));
        reqData.put("out_trade_no", order.getSn());
        reqData.put("total_fee", String.valueOf(order.getPayAmount().multiply(new BigDecimal(100)).intValue()));
        reqData.put("spbill_create_ip", RequestUtil.getRequestIp(request));
        reqData.put("trade_type", tradeType);
        reqData.put("time_expire", DateUtil.format(DateUtil.offsetMinute(order.getCreateDate(), order.getExpireMinute()), "yyyyMMddHHmmss"));
        if (tradeType.equals("JSAPI")) {
            OauthDO oauthInfo = oauthController.info().getData();
            reqData.put("openid", oauthInfo.getOpenId());
        }
        Map<String, String> wxResponse = wxPay.unifiedOrder(reqData);
        if (!wxResponse.get("return_code").equals("SUCCESS")) {
            throw new ApiException(wxResponse.toString());
        }
        payment.setPaymentType(PaymentDO.PaymentTypeEnum.WX_PAY.name());
        payment.setTradeType(wxResponse.get("trade_type"));
        payment.setPrepayId(wxResponse.get("prepay_id"));
        payment.setAppId(wxResponse.get("appid"));
        payment.setMchId(wxResponse.get("mch_id"));
        payment.setOpenId(wxResponse.get("openid"));
        return wxResponse.get("prepay_id");
    }

    private String aliPay(OrderDO order, PaymentDO payment) throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient(AliPayConfig.API_URL, AliPayConfig.APP_ID, AliPayConfig.PRIVATE_KEY, AlipayConstants.FORMAT_JSON, AlipayConstants.CHARSET_GBK, AliPayConfig.PUBLIC_KEY, AlipayConstants.SIGN_TYPE_RSA2);
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        JSONObject json = new JSONObject();
        json.put("subject", String.valueOf(order.getId()));
        json.put("out_trade_no", order.getSn());
        json.put("total_amount", order.getPayAmount().toString());
        json.put("product_code", "QUICK_WAP_WAY");
        json.put("time_expire", DateUtil.format(DateUtil.offsetMinute(order.getCreateDate(), order.getExpireMinute()), "yyyy-MM-dd HH:mm"));
        if (order.getType().equals(OrderDO.TypeEnum.NORMAL.name())) {
            request.setReturnUrl(AliPayConfig.RETURN_URL1 + order.getId());
        } else if (order.getType().equals(OrderDO.TypeEnum.CROWD.name())) {
            request.setReturnUrl(AliPayConfig.RETURN_URL2 + order.getCrowdId());
        }
        request.setNotifyUrl(AliPayConfig.NOTIFY_URL);
        request.setBizContent(json.toJSONString());
        AlipayTradeWapPayResponse aliResponse = alipayClient.pageExecute(request);
        if (!aliResponse.isSuccess()) {
            throw new ApiException(aliResponse.getBody());
        }
        payment.setPaymentType(PaymentDO.PaymentTypeEnum.ALI_PAY.name());
        payment.setTradeType("QUICK_WAP_WAY");
        payment.setPrepayId("0");
        payment.setAppId(AliPayConfig.APP_ID);
        payment.setMchId("0");
        return aliResponse.getBody();
    }

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<OrderDO>> list(PageModel page, OrderQuery query) {
        query.setMemberId(loginMemberHolder.getLoginId());
        Page<OrderDO> pager = (Page<OrderDO>) orderService.page(page, QueryUtil.buildWrapper(query));
        List<String> orderSns = new ArrayList<>();
        Map<String, OrderDO> orderMap = new ConcurrentHashMap<>();
        for (OrderDO order : pager.getRecords()) {
            orderSns.add(order.getSn());
            orderMap.put(order.getSn(), order);
        }
        if (orderSns.size() == 0) return Response.restResult(pager, ResultCode.SUCCESS);
        OrderItemQuery itemQuery = new OrderItemQuery();
        itemQuery.setOrderSn(orderSns.toArray(new String[0]));
        List<OrderItemDO> orderItemList = orderItemService.list(QueryUtil.buildWrapper(itemQuery));
        for (OrderItemDO orderItem : orderItemList) {
            if (orderMap.get(orderItem.getOrderSn()) != null) {
                orderMap.get(orderItem.getOrderSn()).getOrderItemList().add(orderItem);
            }
        }
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<OrderDO> select(@PathVariable Long id) {
        OrderDO order = orderService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, order);
        OrderItemQuery itemQuery = new OrderItemQuery();
        itemQuery.setOrderSn(new String[]{order.getSn()});
        List<OrderItemDO> orderItemList = orderItemService.list(QueryUtil.buildWrapper(itemQuery));
        order.getOrderItemList().addAll(orderItemList);
        if (order.getType().equals(OrderDO.TypeEnum.CROWD.name())) {
            CrowdGroupDO crowdGroup = crowdGroupService.getById(order.getCrowdId());
            order.setCrowdGroup(crowdGroup);
        }
        return Response.restResult(order, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "取消订单")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public R cancel(Long id) {
        OrderDO order = orderService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, order);
        Asserts.eq(order.getStatus(), OrderDO.StatusEnum.WAIT_PAY.name(), "订单状态异常");
        MemberDO member = loginMemberHolder.getLoginMember();
        Asserts.eq(order.getMemberId(), member.getId(), "订单不属于本人");
        orderService.doClose(order);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "删除订单")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R delete(Long id) {
        OrderDO order = orderService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, order);
        Asserts.eq(order.getStatus(), OrderDO.StatusEnum.CLOSED.name(), "订单状态异常");
        MemberDO member = loginMemberHolder.getLoginMember();
        Asserts.eq(order.getMemberId(), member.getId(), "订单不属于本人");
        orderService.doDelete(order.getSn());
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "修改收货地址")
    @RequestMapping(value = "/update/address", method = RequestMethod.POST)
    public R updateAddress(Long orderId, String province, String city, String district, String address, String name, String mobile) {
        OrderDO order = orderService.getById(orderId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, order);
        MemberDO member = loginMemberHolder.getLoginMember();
        Asserts.eq(order.getMemberId(), member.getId(), "订单不属于本人");
        OrderDO entity = new OrderDO();
        entity.setId(orderId);
        entity.setProvince(province);
        entity.setCity(city);
        entity.setDistrict(district);
        entity.setAddress(address);
        entity.setName(name);
        entity.setMobile(mobile);
        orderService.updateById(entity);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
