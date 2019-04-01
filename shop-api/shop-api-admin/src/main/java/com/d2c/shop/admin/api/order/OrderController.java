package com.d2c.shop.admin.api.order;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.query.OrderQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "订单管理")
@RestController
@RequestMapping("/back/order")
public class OrderController extends BaseCtrl<OrderDO, OrderQuery> {

}
