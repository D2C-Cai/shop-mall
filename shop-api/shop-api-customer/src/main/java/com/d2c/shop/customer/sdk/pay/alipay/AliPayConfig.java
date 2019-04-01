package com.d2c.shop.customer.sdk.pay.alipay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Cai
 */
@Component
public class AliPayConfig {

    public static String API_URL;
    public static String APP_ID;
    public static String PRIVATE_KEY;
    public static String PUBLIC_KEY;
    public static String RETURN_URL1;
    public static String RETURN_URL2;
    public static String NOTIFY_URL;

    @Value("${shop.pay.alipay.api-url}")
    public void setApiUrl(String apiUrl) {
        API_URL = apiUrl;
    }

    @Value("${shop.pay.alipay.return-url1}")
    public void setReturnUrl1(String returnUrl) {
        RETURN_URL1 = returnUrl;
    }

    @Value("${shop.pay.alipay.return-url2}")
    public void setReturnUrl2(String returnUrl) {
        RETURN_URL2 = returnUrl;
    }

    @Value("${shop.pay.alipay.notify-url}")
    public void setNotifyUrl(String notifyUrl) {
        NOTIFY_URL = notifyUrl;
    }

    @Value("${shop.pay.alipay.app-id}")
    public void setAppId(String appId) {
        APP_ID = appId;
    }

    @Value("${shop.pay.alipay.private-key}")
    public void setPrivateKey(String privateKey) {
        PRIVATE_KEY = privateKey;
    }

    @Value("${shop.pay.alipay.public-key}")
    public void setPublicKey(String publicKey) {
        PUBLIC_KEY = publicKey;
    }

}
