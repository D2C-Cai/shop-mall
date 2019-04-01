package com.d2c.shop.service.modules.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.shop.service.modules.product.model.ProductSkuDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author BaiCai
 */
public interface ProductSkuMapper extends BaseMapper<ProductSkuDO> {

    int doDeductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    int doReturnStock(@Param("id") Long id, @Param("quantity") Integer quantity);

}
