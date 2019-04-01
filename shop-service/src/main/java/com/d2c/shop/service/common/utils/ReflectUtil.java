package com.d2c.shop.service.common.utils;

import cn.hutool.core.bean.copier.CopyOptions;
import com.d2c.shop.service.common.api.constant.FieldConstant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Cai
 */
public class ReflectUtil {

    // 获取基类和父类所有的字段
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    // 清空BaseDO的公共字段
    public static CopyOptions clearPublicFields() {
        CopyOptions options = CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreError(true)
                .setIgnoreProperties(FieldConstant.ID,
                        FieldConstant.CREATE_DATE,
                        FieldConstant.CREATE_MAN,
                        FieldConstant.MODIFY_DATE,
                        FieldConstant.MODIFY_MAN,
                        FieldConstant.DELETED);
        return options;
    }

}
