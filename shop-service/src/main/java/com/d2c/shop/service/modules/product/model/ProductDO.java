package com.d2c.shop.service.modules.product.model;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.annotation.Prevent;
import com.d2c.shop.service.common.api.base.extension.BaseDelDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.d2c.shop.service.modules.logger.nosql.mongodb.support.IUoLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("P_PRODUCT")
@ApiModel(description = "商品表")
public class ProductDO extends BaseDelDO implements IUoLog {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @ApiModelProperty(value = "条码")
    private String sn;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "名称")
    private String name;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "图片")
    private String pic;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "销售价")
    private BigDecimal price;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品库存")
    private Integer stock;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "品类ID")
    private Long categoryId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "分类ID")
    private Long classifyId;
    @ApiModelProperty(value = "描述")
    private String description;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "虚拟 1,0")
    private Integer virtual;
    @ApiModelProperty(value = "拼团 1,0,-1")
    private Integer crowd;
    @ApiModelProperty(value = "拼团价")
    private BigDecimal crowdPrice;
    @ApiModelProperty(value = "拼团开始时间")
    private Date crowdStartDate;
    @ApiModelProperty(value = "拼团结束时间")
    private Date crowdEndDate;
    @ApiModelProperty(value = "拼团的成团时间")
    private Integer crowdGroupTime;
    @ApiModelProperty(value = "拼团的商品X人团")
    private Integer crowdGroupNum;
    @ApiModelProperty(value = "拼团优惠券ID")
    private Long couponId;
    @TableField(exist = false)
    @ApiModelProperty(value = "品类树")
    private ProductCategoryDO category;
    @TableField(exist = false)
    @ApiModelProperty(value = "分类树")
    private ProductClassifyDO classify;
    @TableField(exist = false)
    @ApiModelProperty(value = "商品的SKU列表")
    private List<ProductSkuDO> skuList = new ArrayList<>();

    public String getFirstPic() {
        if (StrUtil.isBlank(pic)) return null;
        return pic.split(",")[0];
    }

    public boolean crowding() {
        if (crowd == null || crowdStartDate == null || crowdEndDate == null) return false;
        Date now = new Date();
        if (crowd == 1 && now.after(crowdStartDate) && now.before(crowdEndDate)) return true;
        return false;
    }

    @Override
    public String getInfo() {
        return String.valueOf(this.price);
    }

    @Override
    public Long getTargetId() {
        return this.getId();
    }

}
