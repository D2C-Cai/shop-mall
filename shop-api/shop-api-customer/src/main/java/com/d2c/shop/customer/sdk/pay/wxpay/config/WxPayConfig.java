package com.d2c.shop.customer.sdk.pay.wxpay.config;

import com.d2c.shop.customer.sdk.pay.wxpay.IWXPayDomain;
import com.d2c.shop.customer.sdk.pay.wxpay.WXPayConfig;
import com.d2c.shop.customer.sdk.pay.wxpay.WXPayConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author Cai
 */
@Component
public class WxPayConfig extends WXPayConfig {

    public static String NOTIFY_URL;
    private static String APP_ID;
    private static String MCH_ID;
    private static String SECRET_KEY;

    @Value("${shop.pay.wxpay.app-id}")
    public void setAppId(String appId) {
        APP_ID = appId;
    }

    @Value("${shop.pay.wxpay.mch-id}")
    public void setMchId(String mchId) {
        MCH_ID = mchId;
    }

    @Value("${shop.pay.wxpay.secret-key}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    @Value("${shop.pay.wxpay.notify-url}")
    public void setNotifyUrl(String notifyUrl) {
        NOTIFY_URL = notifyUrl;
    }

    @Override
    protected String getAppID() {
        return APP_ID;
    }

    @Override
    protected String getMchID() {
        return MCH_ID;
    }

    @Override
    protected String getKey() {
        return SECRET_KEY;
    }

    @Override
    protected InputStream getCertStream() {
        return null;
    }

    @Override
    protected IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {
            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
    }

}
