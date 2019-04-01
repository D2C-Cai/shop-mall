package com.d2c.shop.service.rabbitmq.sender;

import cn.hutool.core.date.DateUtil;
import com.d2c.shop.service.config.RabbitmqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Cai
 */
@Slf4j
@Component
public class OrderDelayedSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(String msg, Long seconds) {
        log.info("订单编号：" + msg + " 发送时间：" + DateUtil.formatDateTime(new Date()));
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_NAME, RabbitmqConfig.ORDER_QUEUE_NAME, msg, message -> {
            message.getMessageProperties().setHeader("x-delay", seconds * 1000);
            return message;
        });
    }

}
