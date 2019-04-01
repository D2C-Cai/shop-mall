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
public class UserRoleQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @Condition(condition = ConditionEnum.IN, field = "user_id")
    @ApiModelProperty(value = "用户ID")
    private Long[] userIds;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "角色ID")
    private Long roleId;
    @Condition(condition = ConditionEnum.IN, field = "role_id")
    @ApiModelProperty(value = "角色ID")
    private Long[] roleIds;

}
