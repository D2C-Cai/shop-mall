package com.d2c.shop.service.modules.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.product.model.CouponProductDO;

import java.util.List;

/**
 * @author BaiCai
 */
public interface CouponProductService extends IService<CouponProductDO> {

    List<CouponProductDO> findQualified(List<Long> couponIds, List<Long> productIds, Integer type);

}
