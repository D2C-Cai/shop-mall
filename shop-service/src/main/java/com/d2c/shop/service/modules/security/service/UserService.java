package com.d2c.shop.service.modules.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.security.model.UserDO;

import java.util.Date;

/**
 * @author BaiCai
 */
public interface UserService extends IService<UserDO> {

    UserDO findByUsername(String username);

    boolean doLogin(String username, String loginIp, String accessToken, Date accessExpired);

    boolean doLogout(String username);

}
