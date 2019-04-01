package com.d2c.shop.service.modules.member.service.impl;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.modules.member.mapper.MemberCouponMapper;
import com.d2c.shop.service.modules.member.model.MemberCouponDO;
import com.d2c.shop.service.modules.member.service.MemberCouponService;
import com.d2c.shop.service.modules.product.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiCai
 */
@Service
public class MemberCouponServiceImpl extends BaseService<MemberCouponMapper, MemberCouponDO> implements MemberCouponService {

    @Autowired
    private CouponService couponService;

    @Override
    @Transactional
    public MemberCouponDO doReceive(MemberCouponDO memberCoupon) {
        int success = couponService.updateConsumption(memberCoupon.getCouponId());
        if (success == 0) {
            throw new ApiException("优惠券已被一抢而空");
        }
        this.save(memberCoupon);
        return memberCoupon;
    }

}
