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
public class MenuQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.LIKE)
    @ApiModelProperty(value = "名称")
    private String name;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "Ant路径")
    private String path;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "类型")
    private Integer type;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "父级ID")
    private Long parentId;

}
