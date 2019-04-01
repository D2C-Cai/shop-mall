package com.d2c.shop.service.modules.order.model;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.annotation.Prevent;
import com.d2c.shop.service.common.api.base.extension.BaseDelDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.d2c.shop.service.modules.order.model.support.ITradeItem;
import com.d2c.shop.service.modules.product.model.ProductDO;
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
@TableName("O_ORDER_ITEM")
@ApiModel(description = "订单明细表")
public class OrderItemDO extends BaseDelDO implements ITradeItem {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺名")
    private String shopName;
    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "会员ID")
    private Long memberId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "会员账号")
    private String memberAccount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品ID")
    private Long productId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品SKU的ID")
    private Long productSkuId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品数量")
    private Integer quantity;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品规格")
    private String standard;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品名称")
    private String productName;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品图片")
    private String productPic;
    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "订单ID")
    private Long orderId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "订单号")
    private String orderSn;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "类型")
    private String type;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态")
    private String status;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "虚拟 1,0")
    private Integer virtual;
    @ApiModelProperty(value = "支付方式")
    private String paymentType;
    @ApiModelProperty(value = "支付流水")
    private String paymentSn;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品单价")
    private BigDecimal productPrice;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "实时单价")
    private BigDecimal realPrice;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "实际支付")
    private BigDecimal payAmount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "优惠券加权折减")
    private BigDecimal couponWeightingAmount;
    @ApiModelProperty(value = "拼团团ID")
    private Long crowdId;
    @ApiModelProperty(value = "物流公司")
    private String logisticsCom;
    @ApiModelProperty(value = "物流单号")
    private String logisticsNum;
    @TableField(exist = false)
    @ApiModelProperty(value = "类型名")
    private String typeName;
    @TableField(exist = false)
    @ApiModelProperty(value = "状态名")
    private String statusName;
    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式名")
    private String paymentTypeName;
    @TableField(exist = false)
    @ApiModelProperty(value = "活动商品")
    private ProductDO product;
    @TableField(exist = false)
    @ApiModelProperty(value = "拼团团组")
    private CrowdGroupDO crowdGroup;

    public String getTypeName() {
        if (StrUtil.isBlank(type)) return "";
        return OrderDO.TypeEnum.valueOf(type).getDescription();
    }

    public String getStatusName() {
        if (StrUtil.isBlank(status)) return "";
        return StatusEnum.valueOf(status).getDescription();
    }

    public String getPaymentTypeName() {
        if (StrUtil.isBlank(paymentType)) return "";
        return PaymentDO.PaymentTypeEnum.valueOf(paymentType).getDescription();
    }

    public enum StatusEnum {
        //
        WAIT_PAY("待付款"), PAID("已付款"), WAIT_DELIVER("待发货"),
        DELIVERED("已发货"), RECEIVED("已收货"), SUCCESS("交易成功"),
        WAIT_REFUND("待退款"), REFUNDED("已退款"), CLOSED("交易关闭");
        //
        private String description;

        StatusEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
