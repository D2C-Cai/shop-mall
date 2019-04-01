package com.d2c.shop.service.modules.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.shop.service.modules.core.model.ShopDO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author BaiCai
 */
public interface ShopMapper extends BaseMapper<ShopDO> {

    int updateBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

}
