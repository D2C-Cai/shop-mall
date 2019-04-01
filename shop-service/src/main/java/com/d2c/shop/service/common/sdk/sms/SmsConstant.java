package com.d2c.shop.service.common.sdk.sms;

/**
 * @author BaiCai
 */
public interface SmsConstant {

    /**
     * 签名加密key
     */
    String SIGN_KEY = "3dcb6a2698036913d0bf235a448b9118";
    /**
     * 商户签名
     */
    String ACCOUNT_SIGN = "赋能店铺";
    /**
     * 短信验证码模板
     */
    String CODE_TEMPLATE = "您的短信验证码为：${code}，请于15分钟内输入验证，请勿向他人泄露。工作人员不会以任何方式向您索要短信验证码，谨防欺诈短信。";

}
