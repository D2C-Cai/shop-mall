package com.d2c.shop.service.modules.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.shop.service.modules.product.model.CouponDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author BaiCai
 */
public interface CouponMapper extends BaseMapper<CouponDO> {

    int updateConsumption(@Param("id") Long id);

}
