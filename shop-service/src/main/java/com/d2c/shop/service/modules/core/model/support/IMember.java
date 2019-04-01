package com.d2c.shop.service.modules.core.model.support;

import java.util.Date;

/**
 * @author Cai
 */
public interface IMember {

    String getAccount();

    String getPassword();

    String getNickname();

    String getAvatar();

    String getAccessToken();

    Date getAccessExpired();

    String getRegisterIp();

    Date getLoginDate();

    String getLoginIp();

}
