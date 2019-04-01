package com.d2c.shop.service.modules.member.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@TableName("M_OAUTH")
@ApiModel(description = "授权表")
public class OauthDO extends BaseDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "渠道")
    private String channel;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "openID")
    private String openId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "unionID")
    private String unionId;
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "令牌")
    private String accessToken;
    @ApiModelProperty(value = "令牌时效")
    private Date accessExpired;
    @ApiModelProperty(value = "绑定账号")
    private String account;
    @TableField(exist = false)
    @ApiModelProperty(value = "登录返回token")
    private String oauthToken;
    @TableField(exist = false)
    @ApiModelProperty(value = "绑定的登录账号")
    private MemberDO member;

    @JsonIgnore
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public enum ChannelEnum {
        QQ, WECHAT
    }

}
