package com.d2c.shop.service.modules.order.query;

import com.d2c.shop.service.common.api.annotation.Condition;
import com.d2c.shop.service.common.api.base.BaseQuery;
import com.d2c.shop.service.common.api.emuns.ConditionEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderItemQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "店铺名")
    private String shopName;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "会员ID")
    private Long memberId;
    @Condition(condition = ConditionEnum.IN)
    @ApiModelProperty(value = "订单号")
    private String[] orderSn;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "类型")
    private String type;
    @Condition(condition = ConditionEnum.IN)
    @ApiModelProperty(value = "状态")
    private String[] status;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "虚拟 1,0")
    private Integer virtual;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "拼团团ID")
    private Long crowdId;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "商品名称")
    private String productName;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "会员账号")
    private String memberAccount;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "支付方式")
    private String paymentType;

}
