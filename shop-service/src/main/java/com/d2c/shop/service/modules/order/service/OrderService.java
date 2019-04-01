package com.d2c.shop.service.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.model.PaymentDO;
import com.d2c.shop.service.modules.order.query.OrderQuery;

import java.util.Map;

/**
 * @author BaiCai
 */
public interface OrderService extends IService<OrderDO> {

    OrderDO doCreate(OrderDO order);

    boolean doPayment(OrderDO order, PaymentDO.PaymentTypeEnum paymentType, String paymentSn, String mchId);

    boolean doClose(OrderDO order);

    boolean doDelete(String orderSn);

    Map<String, Object> countDaily(OrderQuery query);

}
