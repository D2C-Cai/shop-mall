package com.d2c.shop.service.modules.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.product.model.CouponDO;

/**
 * @author BaiCai
 */
public interface CouponService extends IService<CouponDO> {

    int updateConsumption(Long id);

}
