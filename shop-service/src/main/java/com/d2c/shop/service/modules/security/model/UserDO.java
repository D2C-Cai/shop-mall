package com.d2c.shop.service.modules.security.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.extension.BaseDelDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("SYS_USER")
@ApiModel(description = "用户表")
public class UserDO extends BaseDelDO {

    @Excel(name = "账号")
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "账号")
    private String username;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "密码")
    private String password;
    @Excel(name = "状态", replace = {"正常_1", "禁用_0"})
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
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
    @TableField(exist = false)
    @ApiModelProperty(value = "用户拥有的角色")
    private List<RoleDO> roles = new ArrayList<>();
    @TableField(exist = false)
    @ApiModelProperty(value = "用户拥有的菜单")
    private List<MenuDO> menus = new ArrayList<>();

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
