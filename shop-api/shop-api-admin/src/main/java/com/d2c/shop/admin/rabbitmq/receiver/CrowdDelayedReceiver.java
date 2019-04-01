package com.d2c.shop.admin.rabbitmq.receiver;

import cn.hutool.core.date.DateUtil;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.config.RabbitmqConfig;
import com.d2c.shop.service.modules.logger.nosql.elasticsearch.document.MqErrorLog;
import com.d2c.shop.service.modules.logger.nosql.elasticsearch.repository.MqErrorLogRepository;
import com.d2c.shop.service.modules.member.model.DummyDO;
import com.d2c.shop.service.modules.member.service.DummyService;
import com.d2c.shop.service.modules.order.model.CrowdGroupDO;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.order.query.OrderItemQuery;
import com.d2c.shop.service.modules.order.service.CrowdGroupService;
import com.d2c.shop.service.modules.order.service.OrderItemService;
import com.d2c.shop.service.rabbitmq.sender.CrowdDelayedSender;
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
@RabbitListener(queues = RabbitmqConfig.CROWD_QUEUE_NAME)
public class CrowdDelayedReceiver {

    @Autowired
    private CrowdGroupService crowdGroupService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private DummyService dummyService;
    @Autowired
    private CrowdDelayedSender crowdDelayedSender;
    @Autowired
    private MqErrorLogRepository mqErrorLogRepository;

    @RabbitHandler
    public void process(String msg) {
        log.info("拼团团组ID：" + msg + " 接收时间：" + DateUtil.formatDateTime(new Date()));
        try {
            this.doSomething(msg);
        } catch (Exception e) {
            MqErrorLog error = new MqErrorLog();
            error.setStatus(0);
            error.setId(System.currentTimeMillis());
            error.setTime(LocalDateTime.now().toString());
            error.setType(RabbitmqConfig.CROWD_QUEUE_NAME);
            error.setMessage(msg);
            mqErrorLogRepository.save(error);
            log.error(e.getMessage(), e);
        }
    }

    private void doSomething(String msg) {
        Long crowdId = Long.valueOf(msg);
        CrowdGroupDO crowdGroup = crowdGroupService.getById(crowdId);
        if (crowdGroup != null && crowdGroup.getStatus() == 0) {
            OrderItemQuery oiq = new OrderItemQuery();
            oiq.setCrowdId(crowdId);
            oiq.setStatus(new String[]{OrderItemDO.StatusEnum.WAIT_PAY.name()});
            int num = orderItemService.count(QueryUtil.buildWrapper(oiq));
            if (num > 0) {
                // 如果团组到时间了仍存在未付款成员，则延迟5分钟后再处理
                crowdDelayedSender.send(String.valueOf(crowdId), 5 * 60L);
                return;
            }
            CrowdGroupDO entity = new CrowdGroupDO();
            entity.setId(crowdGroup.getId());
            if (crowdGroup.getPaidNum() == 0) {
                // 如果团组没有一人付款，则直接关闭处理
                entity.setStatus(-1);
                crowdGroupService.updateById(entity);
                return;
            }
            entity.setStatus(1);
            entity.setAttendNum(crowdGroup.getCrowdNum());
            entity.setPaidNum(crowdGroup.getCrowdNum());
            // 虚拟假人头像昵称
            entity.setAvatars(crowdGroup.getAvatars());
            Integer distance = crowdGroup.getCrowdNum() - crowdGroup.getPaidNum();
            List<DummyDO> dummy = dummyService.findRandom(distance);
            for (DummyDO dd : dummy) {
                entity.setAvatars(entity.pushAvatars(0L, dd.getNickname(), dd.getAvatar()));
            }
            crowdGroupService.updateById(entity);
            OrderItemDO noi = new OrderItemDO();
            if (crowdGroup.getVirtual() == 1) {
                // 虚拟商品明细状态-已发货
                noi.setStatus(OrderItemDO.StatusEnum.DELIVERED.name());
            } else {
                // 普通商品明细状态-待发货
                noi.setStatus(OrderItemDO.StatusEnum.WAIT_DELIVER.name());
            }
            OrderItemQuery noiq = new OrderItemQuery();
            noiq.setCrowdId(crowdId);
            noiq.setStatus(new String[]{OrderItemDO.StatusEnum.PAID.name()});
            orderItemService.update(noi, QueryUtil.buildWrapper(noiq));
        }
    }

}
