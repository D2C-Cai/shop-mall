package com.d2c.shop.service.modules.product.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.modules.product.mapper.CouponProductMapper;
import com.d2c.shop.service.modules.product.model.CouponProductDO;
import com.d2c.shop.service.modules.product.service.CouponProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author BaiCai
 */
@Service
public class CouponProductServiceImpl extends BaseService<CouponProductMapper, CouponProductDO> implements CouponProductService {

    @Autowired
    private CouponProductMapper couponProductMapper;

    @Override
    public List<CouponProductDO> findQualified(List<Long> couponIds, List<Long> productIds, Integer type) {
        return couponProductMapper.findQualified(couponIds, productIds, type);
    }

}
