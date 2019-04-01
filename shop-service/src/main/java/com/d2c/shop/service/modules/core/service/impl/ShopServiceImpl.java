package com.d2c.shop.service.modules.core.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.modules.core.mapper.ShopMapper;
import com.d2c.shop.service.modules.core.model.ShopDO;
import com.d2c.shop.service.modules.core.service.ShopService;
import com.d2c.shop.service.modules.core.service.ShopkeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author BaiCai
 */
@Service
public class ShopServiceImpl extends BaseService<ShopMapper, ShopDO> implements ShopService {

    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ShopkeeperService shopkeeperService;

    @Override
    @Transactional
    public ShopDO doCreate(ShopDO shop, String account) {
        this.save(shop);
        shopkeeperService.updateShopId(account, shop.getId());
        return shop;
    }

    @Override
    @Transactional
    public int updateBalance(Long id, BigDecimal amount) {
        return shopMapper.updateBalance(id, amount);
    }

}
