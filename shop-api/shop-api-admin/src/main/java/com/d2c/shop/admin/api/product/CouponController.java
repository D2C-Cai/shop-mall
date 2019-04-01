package com.d2c.shop.admin.api.product;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.product.model.CouponDO;
import com.d2c.shop.service.modules.product.query.CouponQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "优惠券管理")
@RestController
@RequestMapping("/back/coupon")
public class CouponController extends BaseCtrl<CouponDO, CouponQuery> {

}
