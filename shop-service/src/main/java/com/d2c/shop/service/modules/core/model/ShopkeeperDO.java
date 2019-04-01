package com.d2c.shop.service.modules.core.model;

import cn.hutool.core.util.StrUtil;
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

import java.util.Date;

/**
 * @author Cai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("CORE_SHOP_KEEPER")
@ApiModel(description = "店铺员工表")
public class ShopkeeperDO extends BaseDelDO implements IMember {

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
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "角色")
    private String role;
    @ApiModelProperty(value = "备注")
    private String remark;
    @TableField(exist = false)
    @ApiModelProperty(value = "角色名")
    private String roleName;
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

    public String getRoleName() {
        if (StrUtil.isBlank(role)) return "";
        return RoleEnum.valueOf(role).getDescription();
    }

    public enum RoleEnum {
        //
        BOSS("店主"), CLERK("店员");
        // 角色描述
        private String description;

        RoleEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
