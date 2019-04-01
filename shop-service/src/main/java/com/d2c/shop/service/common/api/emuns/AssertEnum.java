package com.d2c.shop.service.common.api.emuns;

/**
 * @author BaiCai
 */
public enum AssertEnum {
    //
    NOT_NULL("值不能为NULL"),
    NULL("值必须为NULL"),
    EQ("值必须等于="),
    GT("值必须大于>"),
    GE("值必须大于等于>="),
    LT("值必须小于<"),
    LE("值必须小于等于<="),
    REGEX("值必须符合正则表达式");
    //
    private String description;

    AssertEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
