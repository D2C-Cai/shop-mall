package com.d2c.shop.service.modules.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.member.model.OauthDO;

import java.util.Date;

/**
 * @author BaiCai
 */
public interface OauthService extends IService<OauthDO> {

    OauthDO findByUnionId(String unionId);

    boolean doLogin(String unionId, String nickname, String avatar, String accessToken, Date accessExpired);

}
