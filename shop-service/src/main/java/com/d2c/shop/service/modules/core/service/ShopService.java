package com.d2c.shop.service.modules.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.core.model.ShopDO;

import java.math.BigDecimal;

/**
 * @author BaiCai
 */
public interface ShopService extends IService<ShopDO> {

    ShopDO doCreate(ShopDO shop, String account);

    int updateBalance(Long id, BigDecimal amount);

}
