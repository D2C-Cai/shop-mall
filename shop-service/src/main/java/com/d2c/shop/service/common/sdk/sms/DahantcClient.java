package com.d2c.shop.service.common.sdk.sms;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;

/**
 * @author Cai
 */
@Component
public class DahantcClient {

    private static String URL = "http://www.dh3t.com/json/sms/Submit";
    private static String ACCOUNT;
    private static String PASSWORD;
    private static RestTemplate restTemplate = new RestTemplate();

    @Value("${shop.sms.dahantc.account}")
    public void setACCOUNT(String ACCOUNT) {
        DahantcClient.ACCOUNT = ACCOUNT;
    }

    @Value("${shop.sms.dahantc.password}")
    public void setPASSWORD(String PASSWORD) {
        DahantcClient.PASSWORD = PASSWORD;
    }

    public boolean sendSMS(String phones, String content) {
        JSONObject json = new JSONObject();
        json.put("account", ACCOUNT);
        json.put("password", DigestUtil.md5Hex(PASSWORD));
        json.put("msgid", "");
        json.put("phones", phones);
        json.put("content", content);
        json.put("sign", null);
        json.put("subcode", null);
        json.put("sendtime", null);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json;charset=UTF-8");
            HttpEntity<byte[]> entity = new HttpEntity<>(json.toJSONString().getBytes("utf-8"), headers);
            byte[] bytes = restTemplate.postForObject(URL, entity, byte[].class);
            JSONObject result = JSONObject.parseObject(new String(bytes));
            if (result == null) return false;
            return result.get("result").equals("0");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

}
