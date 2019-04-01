package com.d2c.shop.service.common.utils;

import com.d2c.shop.service.common.api.annotation.Prevent;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.OperateEnum;

import java.lang.reflect.Field;

/**
 * @author Cai
 */
public class PreventUtil {

    // Update时防止更新字段
    public static <T extends BaseDO> void filtrate(T entity, OperateEnum operate) {
        for (Field field : ReflectUtil.getAllFields(entity.getClass())) {
            field.setAccessible(true);
            // 过滤条件标签
            Prevent annotation = field.getAnnotation(Prevent.class);
            if (annotation == null || !annotation.operate().equals(operate)) continue;
            // 校验字段的值
            Object value = null;
            try {
                value = field.get(entity);
            } catch (IllegalAccessException e) {
                break;
            }
            // 根据操作过滤字段
            switch (annotation.operate()) {
                case UPDATE:
                    if (value != null) {
                        try {
                            field.set(entity, null);
                        } catch (IllegalAccessException e) {
                            break;
                        }
                    }
                    break;
                case INSERT:
                    if (value != null) {
                        try {
                            field.set(entity, null);
                        } catch (IllegalAccessException e) {
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
