package com.d2c.shop.service.modules.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;

import java.util.Date;

/**
 * @author BaiCai
 */
public interface ShopkeeperService extends IService<ShopkeeperDO> {

    ShopkeeperDO findByAccount(String account);

    boolean doLogin(String account, String loginIp, String accessToken, Date accessExpired);

    boolean doLogout(String account);

    boolean updateShopId(String account, Long shopId);

    boolean updatePassword(String account, String password);

}
