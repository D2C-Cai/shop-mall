package com.d2c.shop.quartz.jobs;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.quartz.jobs.base.BaseJob;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.query.OrderQuery;
import com.d2c.shop.service.modules.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author Cai
 */
@Slf4j
@Component
public class OrderJob extends BaseJob {

    @Autowired
    private OrderService orderService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        OrderQuery query = new OrderQuery();
        query.setStatus(OrderDO.StatusEnum.WAIT_PAY.name());
        query.setExpireDateR(new Date());
        Page page = new Page(1, 100, false);
        List<OrderDO> list;
        do {
            list = orderService.page(page, QueryUtil.buildWrapper(query)).getRecords();
            for (OrderDO order : list) {
                try {
                    orderService.doClose(order);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return;
                }
            }
        } while (list.size() > 0);
    }

    @Override
    public String getCronExpression() {
        return "0 0/10 * * * ?";
    }

}
