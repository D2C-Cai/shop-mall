package com.d2c.shop.service.common.utils;

import cn.hutool.core.util.NumberUtil;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.OperateEnum;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.d2c.shop.service.common.api.emuns.AssertEnum.*;

/**
 * @author Cai
 */
public class AssertUtil {

    // Insert时检验字段为空
    public static <T extends BaseDO> void checkout(T entity, OperateEnum operate) {
        for (Field field : ReflectUtil.getAllFields(entity.getClass())) {
            field.setAccessible(true);
            // 校验条件标签
            Assert annotation = field.getAnnotation(Assert.class);
            if (annotation == null || !annotation.operate().equals(operate)) continue;
            // 校验字段的值
            Object value = null;
            try {
                value = field.get(entity);
            } catch (IllegalAccessException e) {
                break;
            }
            // 根据类型校验字段
            switch (annotation.type()) {
                case NOT_NULL:
                    Asserts.notNull(field.getName() + NOT_NULL.getDescription(), value);
                    break;
                case NULL:
                    Asserts.isNull(field.getName() + NULL.getDescription(), value);
                    break;
                case EQ:
                    Asserts.notNull(field.getName() + NOT_NULL.getDescription(), value);
                    if (!NumberUtil.isNumber(value.toString())) {
                        Asserts.eq(annotation.str_value(), value.toString(), field.getName() + EQ.getDescription() + annotation.str_value());
                    } else {
                        Asserts.eq(annotation.num_value(), Integer.valueOf(value.toString()).intValue(), field.getName() + EQ.getDescription() + annotation.num_value());
                    }
                    break;
                case GT:
                    Asserts.notNull(field.getName() + NOT_NULL.getDescription(), value);
                    Asserts.gt(Integer.valueOf(value.toString()).intValue(), annotation.num_value(), field.getName() + GT.getDescription() + annotation.num_value());
                    break;
                case GE:
                    Asserts.notNull(field.getName() + NOT_NULL.getDescription(), value);
                    Asserts.ge(Integer.valueOf(value.toString()).intValue(), annotation.num_value(), field.getName() + GE.getDescription() + annotation.num_value());
                    break;
                case LT:
                    Asserts.notNull(field.getName() + NOT_NULL.getDescription(), value);
                    Asserts.gt(annotation.num_value(), Integer.valueOf(value.toString()).intValue(), field.getName() + LT.getDescription() + annotation.num_value());
                    break;
                case LE:
                    Asserts.notNull(field.getName() + NOT_NULL.getDescription(), value);
                    Asserts.ge(annotation.num_value(), Integer.valueOf(value.toString()).intValue(), field.getName() + LE.getDescription() + annotation.num_value());
                    break;
                case REGEX:
                    Asserts.notNull(field.getName() + NOT_NULL.getDescription(), value);
                    Pattern pattern = Pattern.compile(annotation.reg_ex());
                    Matcher matcher = pattern.matcher(value.toString());
                    if (!matcher.matches()) {
                        Asserts.eq(0, 1, field.getName() + REGEX.getDescription() + annotation.reg_ex());
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
