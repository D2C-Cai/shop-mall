package com.d2c.shop.quartz.jobs.base;

import org.quartz.Job;

/**
 * @author Cai
 */
public abstract class BaseJob implements Job {

    public abstract String getCronExpression();

}
