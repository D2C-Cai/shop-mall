package com.d2c.shop.service.modules.product.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
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
@TableName("P_PRODUCT_CLASSIFY")
@ApiModel(description = "商品分类表")
public class ProductClassifyDO extends BaseDO {

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
    private List<ProductClassifyDO> children = new ArrayList<>();

}
