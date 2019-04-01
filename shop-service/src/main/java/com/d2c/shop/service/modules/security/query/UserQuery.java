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
public class UserQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.LIKE)
    @ApiModelProperty(value = "账号")
    private String username;
    @Condition(condition = ConditionEnum.IN)
    @ApiModelProperty(value = "状态 1,0")
    private Integer[] status;

}
