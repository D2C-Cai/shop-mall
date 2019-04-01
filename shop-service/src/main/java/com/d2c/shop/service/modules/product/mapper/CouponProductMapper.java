package com.d2c.shop.service.modules.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.shop.service.modules.product.model.CouponProductDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiCai
 */
public interface CouponProductMapper extends BaseMapper<CouponProductDO> {

    List<CouponProductDO> findQualified(@Param("couponIds") List<Long> couponIds, @Param("productIds") List<Long> productIds, @Param("type") Integer type);

}
