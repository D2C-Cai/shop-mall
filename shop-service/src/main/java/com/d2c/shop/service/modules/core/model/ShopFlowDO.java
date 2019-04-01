package com.d2c.shop.service.modules.core.model;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.annotation.Prevent;
import com.d2c.shop.service.common.api.base.extension.BaseDelDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.d2c.shop.service.modules.order.model.PaymentDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author Cai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("CORE_SHOP_FLOW")
@ApiModel(description = "店铺流水表")
public class ShopFlowDO extends BaseDelDO {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "订单号")
    private String orderSn;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "支付方式")
    private String paymentType;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "支付流水")
    private String paymentSn;
    @ApiModelProperty(value = "商品名称")
    private String productName;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "类型")
    private String type;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
    @TableField(exist = false)
    @ApiModelProperty(value = "类型名")
    private String typeName;
    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式名")
    private String paymentTypeName;

    public String getTypeName() {
        if (StrUtil.isBlank(type)) return "";
        return TypeEnum.valueOf(type).getDescription();
    }

    public String getPaymentTypeName() {
        if (StrUtil.isBlank(paymentType)) return "";
        return PaymentDO.PaymentTypeEnum.valueOf(this.paymentType).getDescription();
    }

    public enum TypeEnum {
        //
        ORDER("订单"), WITHDRAW("提现");
        //
        private String description;

        TypeEnum(String description) {
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
