package com.d2c.shop.service.modules.product.model;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("P_PRODUCT_CATEGORY")
@ApiModel(description = "商品品类表")
public class ProductCategoryDO extends BaseDO {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "父级ID")
    private Long parentId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "级别")
    private Integer level;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @TableField(exist = false)
    @ApiModelProperty(value = "子级列表")
    private List<ProductCategoryDO> children = new ArrayList<>();

}
