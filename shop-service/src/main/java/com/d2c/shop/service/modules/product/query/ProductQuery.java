package com.d2c.shop.service.modules.product.query;

import com.d2c.shop.service.common.api.annotation.Condition;
import com.d2c.shop.service.common.api.base.BaseQuery;
import com.d2c.shop.service.common.api.emuns.ConditionEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "条码")
    private String sn;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "名称")
    private String name;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "虚拟 1,0")
    private Integer virtual;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "品类ID")
    private Long categoryId;
    @Condition(condition = ConditionEnum.IN, field = "category_id")
    @ApiModelProperty(value = "品类ID")
    private Long[] categoryIds;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "拼团 1,0,-1,8")
    private Integer crowd;
    @Condition(condition = ConditionEnum.GT, field = "crowd_start_date")
    @ApiModelProperty(value = "拼团开始时间起")
    private Date crowdStartDateL;
    @Condition(condition = ConditionEnum.LE, field = "crowd_start_date")
    @ApiModelProperty(value = "拼团开始时间止")
    private Date crowdStartDateR;
    @Condition(condition = ConditionEnum.GE, field = "crowd_end_date")
    @ApiModelProperty(value = "拼团结束时间起")
    private Date crowdEndDateL;
    @Condition(condition = ConditionEnum.LT, field = "crowd_end_date")
    @ApiModelProperty(value = "拼团结束时间止")
    private Date crowdEndDateR;
    @Condition(condition = ConditionEnum.GE, field = "price")
    @ApiModelProperty(value = "最低价格")
    private BigDecimal priceL;
    @Condition(condition = ConditionEnum.LE, field = "price")
    @ApiModelProperty(value = "最高价格")
    private BigDecimal priceR;

}
