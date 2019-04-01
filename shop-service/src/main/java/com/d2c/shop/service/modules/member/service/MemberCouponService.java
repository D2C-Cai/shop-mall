package com.d2c.shop.service.modules.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.member.model.MemberCouponDO;

/**
 * @author BaiCai
 */
public interface MemberCouponService extends IService<MemberCouponDO> {

    MemberCouponDO doReceive(MemberCouponDO memberCoupon);

}
