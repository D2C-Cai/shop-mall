package com.d2c.shop.service.modules.logger.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Cai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("LOG_SMS")
@ApiModel(description = "短信表")
public class SmsDO extends BaseDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "验证码")
    private String code;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "IP地址")
    private String ip;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "过期时间")
    private Date deadline;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;

}
