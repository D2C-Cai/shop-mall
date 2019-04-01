package com.d2c.shop.service.modules.product.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.modules.product.mapper.CouponMapper;
import com.d2c.shop.service.modules.product.model.CouponDO;
import com.d2c.shop.service.modules.product.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiCai
 */
@Service
public class CouponServiceImpl extends BaseService<CouponMapper, CouponDO> implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    @Transactional
    public int updateConsumption(Long id) {
        return couponMapper.updateConsumption(id);
    }

}
