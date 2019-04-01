package com.d2c.shop.service.modules.product.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.annotation.Prevent;
import com.d2c.shop.service.common.api.base.extension.BaseDelDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("P_PRODUCT_SKU")
@ApiModel(description = "商品SKU表")
public class ProductSkuDO extends BaseDelDO {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品ID")
    private Long productId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "规格")
    private String standard;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "销售价")
    private BigDecimal sellPrice;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "虚拟 1,0")
    private Integer virtual;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "库存")
    private Integer stock;
    @ApiModelProperty(value = "预警库存")
    private Integer warnStock;

}
