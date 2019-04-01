package com.d2c.shop.admin.rabbitmq.receiver;

import cn.hutool.core.date.DateUtil;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.config.RabbitmqConfig;
import com.d2c.shop.service.modules.logger.nosql.elasticsearch.document.MqErrorLog;
import com.d2c.shop.service.modules.logger.nosql.elasticsearch.repository.MqErrorLogRepository;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.order.query.OrderItemQuery;
import com.d2c.shop.service.modules.order.query.OrderQuery;
import com.d2c.shop.service.modules.order.service.OrderItemService;
import com.d2c.shop.service.modules.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Cai
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitmqConfig.ORDER_QUEUE_NAME)
public class OrderDelayedReceiver {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private MqErrorLogRepository mqErrorLogRepository;

    @RabbitHandler
    public void process(String msg) {
        log.info("订单编号：" + msg + " 接收时间：" + DateUtil.formatDateTime(new Date()));
        try {
            this.doSomething(msg);
        } catch (Exception e) {
            MqErrorLog error = new MqErrorLog();
            error.setStatus(0);
            error.setId(System.currentTimeMillis());
            error.setTime(LocalDateTime.now().toString());
            error.setType(RabbitmqConfig.ORDER_QUEUE_NAME);
            error.setMessage(msg);
            mqErrorLogRepository.save(error);
            log.error(e.getMessage(), e);
        }
    }

    private void doSomething(String msg) {
        OrderQuery query = new OrderQuery();
        query.setSn(msg);
        OrderDO order = orderService.getOne(QueryUtil.buildWrapper(query));
        if (order != null && order.getStatus().equals(OrderDO.StatusEnum.WAIT_PAY.name())) {
            OrderItemQuery itemQuery = new OrderItemQuery();
            itemQuery.setOrderSn(new String[]{order.getSn()});
            List<OrderItemDO> orderItemList = orderItemService.list(QueryUtil.buildWrapper(itemQuery));
            order.setOrderItemList(orderItemList);
            orderService.doClose(order);
        }
    }

}
