package com.d2c.shop.service.common.api.base;

import com.d2c.shop.service.common.api.annotation.Condition;
import com.d2c.shop.service.common.api.emuns.ConditionEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author BaiCai
 */
@Data
public abstract class BaseQuery implements Serializable {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "唯一主键ID")
    public Long id;
    @Condition(condition = ConditionEnum.GE, field = "create_date")
    @ApiModelProperty(value = "创建时间起")
    public Date createDateL;
    @Condition(condition = ConditionEnum.LE, field = "create_date")
    @ApiModelProperty(value = "创建时间止")
    public Date createDateR;
    @Condition(condition = ConditionEnum.GE, field = "modify_date")
    @ApiModelProperty(value = "修改时间起")
    public Date modifyDateL;
    @Condition(condition = ConditionEnum.LE, field = "modify_date")
    @ApiModelProperty(value = "修改时间止")
    public Date modifyDateR;

}
