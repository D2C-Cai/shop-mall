package com.d2c.shop.service.modules.member.query;

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
public class MemberCouponQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "会员ID")
    private Long memberId;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "优惠券ID")
    private Long couponId;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Condition(condition = ConditionEnum.GE, field = "service_start_date")
    @ApiModelProperty(value = "使用固定期限-开始起")
    private Date serviceStartDateL;
    @Condition(condition = ConditionEnum.LE, field = "service_start_date")
    @ApiModelProperty(value = "使用固定期限-开始止")
    private Date serviceStartDateR;
    @Condition(condition = ConditionEnum.GE, field = "service_end_date")
    @ApiModelProperty(value = "使用固定期限-结束起")
    private Date serviceEndDateL;
    @Condition(condition = ConditionEnum.LT, field = "service_end_date")
    @ApiModelProperty(value = "使用固定期限-结束止")
    private Date serviceEndDateR;

}
