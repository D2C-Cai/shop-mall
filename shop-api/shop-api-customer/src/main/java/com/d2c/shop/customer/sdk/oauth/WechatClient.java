package com.d2c.shop.customer.sdk.oauth;

import cn.hutool.http.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Cai
 */
@Component
public class WechatClient {

    private static String OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    private static String APP_ID;
    private static String APP_SECRET;

    public String getAccessToken(String code) {
        StringBuilder builder = new StringBuilder();
        builder.append(OAUTH2_URL);
        builder.append("?appid=");
        builder.append(APP_ID);
        builder.append("&secret=");
        builder.append(APP_SECRET);
        builder.append("&code=");
        builder.append(code);
        builder.append("&grant_type=authorization_code");
        return HttpUtil.get(builder.toString(), 5000);
    }

    public String getUserInfo(String access_token, String openid) {
        StringBuilder builder = new StringBuilder();
        builder.append(USER_INFO_URL);
        builder.append("?access_token=");
        builder.append(access_token);
        builder.append("&openid=");
        builder.append(openid);
        builder.append("&lang=zh_CN");
        return HttpUtil.get(builder.toString(), 5000);
    }

    @Value("${shop.oauth.wechat.app-id}")
    public void setAppId(String appId) {
        APP_ID = appId;
    }

    @Value("${shop.oauth.wechat.app-secret}")
    public void setAppSecret(String appSecret) {
        APP_SECRET = appSecret;
    }

}
