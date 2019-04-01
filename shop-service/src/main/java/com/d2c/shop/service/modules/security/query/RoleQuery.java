package com.d2c.shop.service.modules.security.query;

import com.d2c.shop.service.common.api.annotation.Condition;
import com.d2c.shop.service.common.api.base.BaseQuery;
import com.d2c.shop.service.common.api.emuns.ConditionEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "ROLE_开头")
    private String code;
    @Condition(condition = ConditionEnum.LIKE)
    @ApiModelProperty(value = "名称")
    private String name;

}
