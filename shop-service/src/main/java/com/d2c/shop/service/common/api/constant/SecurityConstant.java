package com.d2c.shop.service.common.api.constant;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * @author BaiCai
 */
public interface SecurityConstant {

    /**
     * token分割
     */
    String TOKEN_PREFIX = "D2C-";
    /**
     * JWT签名加密key
     */
    String JWT_SIGN_KEY = DigestUtil.md5Hex("shop");
    /**
     * token参数头
     */
    String OAUTH_TOKEN = "oauthToken";
    /**
     * token参数头
     */
    String ACCESS_TOKEN = "accessToken";
    /**
     * author参数头
     */
    String AUTHORITIES = "authorities";
    /**
     * author的token有效期
     */
    Integer OAUTH_OFFSET_DAY = 1;
    /**
     * C端用户的token有效期
     */
    Integer C_OFFSET_DAY = 7;
    /**
     * B端用户的token有效期
     */
    Integer B_OFFSET_DAY = 30;

}
