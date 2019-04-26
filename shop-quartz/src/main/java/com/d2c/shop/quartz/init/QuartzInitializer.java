package com.d2c.shop.quartz.init;

import com.d2c.shop.quartz.handler.QuartzHandler;
import com.d2c.shop.quartz.jobs.base.BaseJob;
import com.d2c.shop.service.common.utils.SpringUtil;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author Cai
 */
@Component
public class QuartzInitializer {

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private QuartzHandler quartzHandler;

    @PostConstruct
    public void init() throws SchedulerException {
        Map<String, BaseJob> map = SpringUtil.getApplicationContext().getBeansOfType(BaseJob.class);
        for (BaseJob job : map.values()) {
            if (scheduler.getJobDetail(new JobKey(job.getClass().getName())) == null) {
                quartzHandler.insert(job.getClass().getName(), job.getCronExpression());
            }
        }
    }

}
