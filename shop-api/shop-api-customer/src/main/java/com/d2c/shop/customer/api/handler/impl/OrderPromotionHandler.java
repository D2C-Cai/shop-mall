package com.d2c.shop.customer.api.handler.impl;

import com.d2c.shop.customer.api.handler.OrderHandler;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Cai
 */
@Component
public class OrderPromotionHandler implements OrderHandler {

    @Override
    public void operator(OrderDO order, Object... conditions) {
        // 商品活动和订单活动
        BigDecimal productAmount = BigDecimal.ZERO;
        for (OrderItemDO orderItem : order.getOrderItemList()) {
            BigDecimal itemAmount = orderItem.getRealPrice().multiply(new BigDecimal(orderItem.getQuantity()));
            productAmount = productAmount.add(itemAmount);
            orderItem.setPayAmount(itemAmount);
        }
        order.setProductAmount(productAmount);
        order.setPayAmount(productAmount);
    }

}
