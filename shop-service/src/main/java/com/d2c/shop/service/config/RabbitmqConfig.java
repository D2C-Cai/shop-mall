package com.d2c.shop.service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cai
 */
@Configuration
public class RabbitmqConfig {

    public final static String EXCHANGE_NAME = "delayed.exchange";
    //
    public final static String ORDER_QUEUE_NAME = "delayed.order.expired";
    public final static String CROWD_QUEUE_NAME = "delayed.crowd.expired";

    @Bean
    public CustomExchange exchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        //参数二为类型：必须是x-delayed-message
        return new CustomExchange(EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean(name = "orderQueue")
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE_NAME);
    }

    @Bean
    public Binding bindingOrder(@Qualifier("orderQueue") Queue queue, CustomExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_QUEUE_NAME).noargs();
    }

    @Bean(name = "crowdQueue")
    public Queue crowdQueue() {
        return new Queue(CROWD_QUEUE_NAME);
    }

    @Bean
    public Binding bindingCrowd(@Qualifier("crowdQueue") Queue queue, CustomExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CROWD_QUEUE_NAME).noargs();
    }

}
