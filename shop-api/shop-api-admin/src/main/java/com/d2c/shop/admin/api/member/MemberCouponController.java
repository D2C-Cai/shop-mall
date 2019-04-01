package com.d2c.shop.admin.api.member;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.member.model.MemberCouponDO;
import com.d2c.shop.service.modules.member.query.MemberCouponQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "会员优惠券关系")
@RestController
@RequestMapping("/back/member_coupon")
public class MemberCouponController extends BaseCtrl<MemberCouponDO, MemberCouponQuery> {

}
