package com.d2c.shop.service.modules.product.model;

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
@TableName("P_COUPON_PRODUCT")
@ApiModel(description = "优惠券商品关系表")
public class CouponProductDO extends BaseDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "优惠券ID")
    private Long couponId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品ID")
    private Long productId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "类型 1,-1")
    private Integer type;

}
