package com.d2c.shop.service.modules.core.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.mapper.ShopkeeperMapper;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.core.query.ShopkeeperQuery;
import com.d2c.shop.service.modules.core.service.ShopkeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author BaiCai
 */
@Service
public class ShopkeeperServiceImpl extends BaseService<ShopkeeperMapper, ShopkeeperDO> implements ShopkeeperService {

    @Autowired
    private ShopkeeperMapper shopkeeperMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Cacheable(value = "SHOPKEEPER", key = "'session:'+#account", unless = "#result == null")
    public ShopkeeperDO findByAccount(String account) {
        ShopkeeperQuery query = new ShopkeeperQuery();
        query.setAccount(account);
        ShopkeeperDO keeper = this.getOne(QueryUtil.buildWrapper(query));
        return keeper;
    }

    @Override
    @CacheEvict(value = "SHOPKEEPER", key = "'session:'+#account")
    public boolean doLogin(String account, String loginIp, String accessToken, Date accessExpired) {
        ShopkeeperDO entity = new ShopkeeperDO();
        entity.setAccessToken(new BCryptPasswordEncoder().encode(accessToken));
        entity.setAccessExpired(accessExpired);
        entity.setLoginDate(new Date());
        entity.setLoginIp(loginIp);
        ShopkeeperQuery query = new ShopkeeperQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "SHOPKEEPER", key = "'session:'+#account")
    public boolean doLogout(String account) {
        ShopkeeperDO entity = new ShopkeeperDO();
        entity.setAccessToken("");
        ShopkeeperQuery query = new ShopkeeperQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "SHOPKEEPER", key = "'session:'+#account")
    public boolean updateShopId(String account, Long shopId) {
        ShopkeeperDO entity = new ShopkeeperDO();
        entity.setShopId(shopId);
        ShopkeeperQuery query = new ShopkeeperQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "SHOPKEEPER", key = "'session:'+#account")
    public boolean updatePassword(String account, String password) {
        ShopkeeperDO entity = new ShopkeeperDO();
        entity.setPassword(password);
        ShopkeeperQuery query = new ShopkeeperQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    public boolean updateById(ShopkeeperDO entity) {
        ShopkeeperDO shopkeeper = shopkeeperMapper.selectById(entity.getId());
        redisTemplate.delete("SHOPKEEPER::session:" + shopkeeper.getAccount());
        return super.updateById(entity);
    }

}
