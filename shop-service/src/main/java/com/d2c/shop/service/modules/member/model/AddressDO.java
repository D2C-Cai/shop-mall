package com.d2c.shop.service.modules.member.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.annotation.Prevent;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.d2c.shop.service.modules.member.model.support.IAddress;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Cai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("M_ADDRESS")
@ApiModel(description = "地址表")
public class AddressDO extends BaseDO implements IAddress {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "会员ID")
    private Long memberId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "会员账号")
    private String memberAccount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "行政编码")
    private String code;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "省份")
    private String province;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "城市")
    private String city;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "区县")
    private String district;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "地址")
    private String address;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "姓名")
    private String name;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "手机")
    private String mobile;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "默认 1,0")
    private Integer defaults;

}
