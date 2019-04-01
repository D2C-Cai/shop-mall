package com.d2c.shop.admin.api.order;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.order.model.PaymentDO;
import com.d2c.shop.service.modules.order.query.PaymentQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "支付单管理")
@RestController
@RequestMapping("/back/payment")
public class PaymentController extends BaseCtrl<PaymentDO, PaymentQuery> {

}
