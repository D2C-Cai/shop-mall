package com.d2c.shop.service.modules.logger.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.common.sdk.sms.DahantcClient;
import com.d2c.shop.service.common.sdk.sms.SmsConstant;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.logger.mapper.SmsMapper;
import com.d2c.shop.service.modules.logger.model.SmsDO;
import com.d2c.shop.service.modules.logger.query.SmsQuery;
import com.d2c.shop.service.modules.logger.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author BaiCai
 */
@Service
public class SmsServiceImpl extends BaseService<SmsMapper, SmsDO> implements SmsService {

    @Autowired
    private DahantcClient dahantcClient;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean doSend(String mobile, String ip) {
        Asserts.isNull("60秒内无法重复发送短信", redisTemplate.opsForValue().get("SMS::" + ip));
        String code = RandomUtil.randomNumbers(4);
        String content = SmsConstant.CODE_TEMPLATE;
        content = content.replace("${code}", code);
        SmsDO sms = new SmsDO();
        sms.setMobile(mobile);
        sms.setCode(code);
        sms.setIp(ip);
        sms.setStatus(1);
        sms.setDeadline(DateUtil.offsetMinute(new Date(), 15));
        redisTemplate.opsForValue().set("SMS::" + ip, mobile, 58, TimeUnit.SECONDS);
        SmsQuery query = new SmsQuery();
        query.setMobile(mobile);
        this.remove(QueryUtil.buildWrapper(query));
        boolean success = this.save(sms);
        if (success) dahantcClient.sendSMS(mobile, content);
        return success;
    }

    @Override
    @Transactional
    public boolean doCheck(String mobile, String code) {
        SmsQuery query = new SmsQuery();
        query.setMobile(mobile);
        query.setCode(code);
        query.setDeadlineL(new Date());
        query.setStatus(1);
        SmsDO old = this.getOne(QueryUtil.buildWrapper(query));
        if (old == null) return false;
        SmsDO sms = new SmsDO();
        sms.setStatus(0);
        sms.setId(old.getId());
        return this.updateById(sms);
    }

}
