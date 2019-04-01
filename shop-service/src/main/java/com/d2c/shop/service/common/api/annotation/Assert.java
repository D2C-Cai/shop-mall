package com.d2c.shop.service.common.api.annotation;

import com.d2c.shop.service.common.api.emuns.AssertEnum;
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
public @interface Assert {

    AssertEnum type() default AssertEnum.NOT_NULL;

    OperateEnum operate() default OperateEnum.INSERT;

    int num_value() default 0;

    String str_value() default "";

    String reg_ex() default "";

}
