package com.d2c.shop.service.common.api.annotation;

import com.d2c.shop.service.common.api.emuns.OperateEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author BaiCai
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Prevent {

    OperateEnum operate() default OperateEnum.UPDATE;

}
