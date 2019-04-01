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
@TableName("SYS_ROLE")
@ApiModel(description = "角色表")
public class RoleDO extends BaseDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "ROLE_开头")
    private String code;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "名称")
    private String name;

}
