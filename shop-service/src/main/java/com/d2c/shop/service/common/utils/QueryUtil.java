package com.d2c.shop.service.common.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.shop.service.common.api.annotation.Condition;
import com.d2c.shop.service.common.api.base.BaseQuery;

import java.lang.reflect.Field;

/**
 * @author BaiCai
 */
public class QueryUtil {

    // 构建QueryWrapper
    public static <T extends BaseQuery> QueryWrapper buildWrapper(T query) {
        return buildWrapper(query, true);
    }

    // 构建QueryWrapper
    public static <T extends BaseQuery> QueryWrapper buildWrapper(T query, boolean introspect) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper();
        // 防止空查询参数
        // 导致的全表查询
        boolean empty = true;
        for (Field field : ReflectUtil.getAllFields(query.getClass())) {
            field.setAccessible(true);
            // 查询条件标签
            Condition annotation = field.getAnnotation(Condition.class);
            if (annotation == null) continue;
            // 数据库查询字段
            String key = annotation.field();
            if (StrUtil.isBlank(key)) {
                key = camelToUnderline(field.getName());
            }
            // 数据库查询的值
            Object value = null;
            try {
                value = field.get(query);
                if (value != null) {
                    empty = false;
                }
            } catch (IllegalAccessException e) {
                break;
            }
            Object[] array = new Object[]{};
            if (value != null && value.getClass().isArray()) {
                array = (Object[]) value;
            }
            // 数据库语句片段
            String sql = annotation.sql();
            // 根据条件构造Wrapper
            switch (annotation.condition()) {
                case EQ:
                    if (value != null) {
                        queryWrapper.eq(key, value);
                    }
                    break;
                case NE:
                    if (value != null) {
                        queryWrapper.ne(key, value);
                    }
                    break;
                case GE:
                    if (value != null) {
                        queryWrapper.ge(key, value);
                    }
                    break;
                case GT:
                    if (value != null) {
                        queryWrapper.gt(key, value);
                    }
                    break;
                case LE:
                    if (value != null) {
                        queryWrapper.le(key, value);
                    }
                    break;
                case LT:
                    if (value != null) {
                        queryWrapper.lt(key, value);
                    }
                    break;
                case LIKE:
                    if (value != null) {
                        queryWrapper.like(key, value);
                    }
                    break;
                case NOT_LIKE:
                    if (value != null) {
                        queryWrapper.notLike(key, value);
                    }
                    break;
                case IN:
                    if (array != null && array.length > 0) {
                        queryWrapper.in(key, array);
                    }
                    break;
                case NOT_IN:
                    if (array != null && array.length > 0) {
                        queryWrapper.notIn(key, array);
                    }
                    break;
                case IS_NULL:
                    if (value != null && (int) value == 1) {
                        queryWrapper.isNull(key);
                    }
                    break;
                case IS_NOT_NULL:
                    if (value != null && (int) value == 1) {
                        queryWrapper.isNotNull(key);
                    }
                    break;
                case EXIST:
                    if (StrUtil.isNotBlank(sql)) {
                        queryWrapper.exists(sql);
                    }
                    break;
                case NOT_EXIST:
                    if (StrUtil.isNotBlank(sql)) {
                        queryWrapper.notExists(sql);
                    }
                    break;
                default:
                    break;
            }
        }
        if (introspect && empty) throw new ApiException("查询参数不可全部为空");
        return queryWrapper;
    }

    // 字段名称驼峰转下划线
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
