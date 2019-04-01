package com.d2c.shop.service.modules.product.query;

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
public class CouponQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "拼团 1,0")
    private Integer crowd;
    @Condition(condition = ConditionEnum.GT, field = "receive_start_date")
    @ApiModelProperty(value = "领取期限-开始起")
    private Date receiveStartDateL;
    @Condition(condition = ConditionEnum.LE, field = "receive_start_date")
    @ApiModelProperty(value = "领取期限-开始止")
    private Date receiveStartDateR;
    @Condition(condition = ConditionEnum.GE, field = "receive_end_date")
    @ApiModelProperty(value = "领取期限-结束起")
    private Date receiveEndDateL;
    @Condition(condition = ConditionEnum.LT, field = "receive_end_date")
    @ApiModelProperty(value = "领取期限-结束止")
    private Date receiveEndDateR;

}
