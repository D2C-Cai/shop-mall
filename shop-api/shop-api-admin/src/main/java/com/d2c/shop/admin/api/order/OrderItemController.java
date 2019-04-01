package com.d2c.shop.admin.api.order;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.order.query.OrderItemQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "订单明细管理")
@RestController
@RequestMapping("/back/order_item")
public class OrderItemController extends BaseCtrl<OrderItemDO, OrderItemQuery> {

}
