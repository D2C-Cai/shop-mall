package com.d2c.shop.service.modules.member.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.extension.BaseDelDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.d2c.shop.service.modules.core.model.support.IMember;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Cai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("M_MEMBER")
@ApiModel(description = "会员表")
public class MemberDO extends BaseDelDO implements IMember {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "账号")
    private String account;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "令牌")
    private String accessToken;
    @ApiModelProperty(value = "令牌时效")
    private Date accessExpired;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "注册IP")
    private String registerIp;
    @ApiModelProperty(value = "最后登录时间")
    private Date loginDate;
    @ApiModelProperty(value = "最后登录IP")
    private String loginIp;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @ApiModelProperty(value = "性别")
    private String sex;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "消费金额")
    private BigDecimal consumeAmount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "消费次数")
    private Integer consumeTimes;
    @TableField(exist = false)
    @ApiModelProperty(value = "登录返回token")
    private String loginToken;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
