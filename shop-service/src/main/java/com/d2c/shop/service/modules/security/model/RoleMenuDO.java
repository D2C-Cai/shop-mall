package com.d2c.shop.service.modules.security.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("SYS_ROLE_MENU")
@ApiModel(description = "角色菜单关系表")
public class RoleMenuDO extends BaseDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "角色ID")
    private Long roleId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "菜单ID")
    private Long menuId;

}
