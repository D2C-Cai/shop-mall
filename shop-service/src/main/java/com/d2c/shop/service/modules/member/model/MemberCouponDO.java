package com.d2c.shop.service.modules.member.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.annotation.Prevent;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
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
@TableName("M_MEMBER_COUPON")
@ApiModel(description = "会员优惠券")
public class MemberCouponDO extends BaseDO {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺名")
    private String shopName;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "会员ID")
    private Long memberId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "优惠券ID")
    private Long couponId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "使用固定期限-开始")
    private Date serviceStartDate;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "使用固定期限-结束")
    private Date serviceEndDate;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @TableField(exist = false)
    @ApiModelProperty(value = "名称")
    private String name;
    @TableField(exist = false)
    @ApiModelProperty(value = "满XX元")
    private BigDecimal needAmount;
    @TableField(exist = false)
    @ApiModelProperty(value = "减XX元")
    private BigDecimal amount;
    @TableField(exist = false)
    @ApiModelProperty(value = "备注说明")
    private String remark;

    public boolean available() {
        Date now = new Date();
        if (status == 1 && now.after(serviceStartDate) && now.before(serviceEndDate)) return true;
        return false;
    }

}
