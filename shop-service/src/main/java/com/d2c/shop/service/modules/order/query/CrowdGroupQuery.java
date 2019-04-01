package com.d2c.shop.service.modules.order.query;

import com.d2c.shop.service.common.api.annotation.Condition;
import com.d2c.shop.service.common.api.base.BaseQuery;
import com.d2c.shop.service.common.api.emuns.ConditionEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CrowdGroupQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "商品ID")
    private Long productId;
    @Condition(condition = ConditionEnum.GE, field = "deadline")
    @ApiModelProperty(value = "过期时间起")
    private Date deadlineL;
    @Condition(condition = ConditionEnum.LE, field = "deadline")
    @ApiModelProperty(value = "过期时间止")
    private Date deadlineR;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;

}
