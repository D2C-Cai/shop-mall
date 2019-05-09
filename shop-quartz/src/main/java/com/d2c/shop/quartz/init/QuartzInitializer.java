package com.d2c.shop.quartz.init;

import com.d2c.shop.quartz.handler.QuartzHandler;
import com.d2c.shop.quartz.jobs.base.BaseJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

/**
 * @author Cai
 */
@Slf4j
@Component
public class QuartzInitializer implements ServletContextListener {

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private QuartzHandler quartzHandler;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        Map<String, BaseJob> map = applicationContext.getBeansOfType(BaseJob.class);
        for (BaseJob job : map.values()) {
            try {
                if (scheduler.getJobDetail(new JobKey(job.getClass().getName())) == null) {
                    quartzHandler.insert(job.getClass().getName(), job.getCronExpression());
                }
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

}
