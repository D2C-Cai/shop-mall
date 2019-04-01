package com.d2c.shop.service.modules.member.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Cai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("M_MEMBER_DUMMY")
@ApiModel(description = "假人表")
public class DummyDO extends BaseDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "头像")
    private String avatar;

}
