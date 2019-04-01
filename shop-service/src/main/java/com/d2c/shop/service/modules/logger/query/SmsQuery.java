package com.d2c.shop.service.modules.logger.query;

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
public class SmsQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "验证码")
    private String code;
    @Condition(condition = ConditionEnum.GE, field = "deadline")
    @ApiModelProperty(value = "过期时间")
    private Date deadlineL;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;

}
