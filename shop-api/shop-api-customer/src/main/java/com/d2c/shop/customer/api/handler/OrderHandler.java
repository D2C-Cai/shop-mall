package com.d2c.shop.customer.api.handler;

import com.d2c.shop.service.modules.order.model.OrderDO;

/**
 * @author Cai
 */
public interface OrderHandler {

    void operator(OrderDO order, Object... conditions);

}
