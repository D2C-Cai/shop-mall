package com.d2c.shop.service.modules.core.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.extension.BaseDelDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.d2c.shop.service.modules.core.model.support.IShop;
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
@TableName("CORE_SHOP")
@ApiModel(description = "店铺表")
public class ShopDO extends BaseDelDO implements IShop {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "Logo")
    private String logo;
    @ApiModelProperty(value = "Banner")
    private String banner;
    @ApiModelProperty(value = "简介")
    private String summary;
    @ApiModelProperty(value = "公告")
    private String notice;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "经营范围")
    private String scope;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "地址")
    private String address;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "电话")
    private String telephone;
    @ApiModelProperty(value = "微信")
    private String wechat;
    @ApiModelProperty(value = "营业时间")
    private String hours;
    @ApiModelProperty(value = "退货地址")
    private String returnAddress;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "认证 1,0")
    private Integer authenticate;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "有效期")
    private Date validDate;
    @ApiModelProperty(value = "企业名称")
    private String enterprise;
    @ApiModelProperty(value = "营业执照号")
    private String licenseNum;
    @ApiModelProperty(value = "营业执照图")
    private String licensePic;
    @ApiModelProperty(value = "法人姓名")
    private String corporationName;
    @ApiModelProperty(value = "法人身份证号")
    private String corporationCard;
    @ApiModelProperty(value = "法人身份证图")
    private String corporationPic;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "资金余额")
    private BigDecimal balance;

}
