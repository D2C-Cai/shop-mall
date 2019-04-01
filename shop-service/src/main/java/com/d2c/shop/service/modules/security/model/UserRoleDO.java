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
@TableName("SYS_USER_ROLE")
@ApiModel(description = "用户角色关系表")
public class UserRoleDO extends BaseDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "角色ID")
    private Long roleId;

}
