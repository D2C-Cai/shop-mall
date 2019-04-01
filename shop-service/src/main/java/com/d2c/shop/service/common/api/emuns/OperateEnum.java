package com.d2c.shop.service.common.api.emuns;

/**
 * @author BaiCai
 */
public enum OperateEnum {
    //
    INSERT("新增"),
    UPDATE("更新"),
    DELETE("删除");
    //
    private String description;

    OperateEnum(String description) {
        this.description = description;
    }
}
