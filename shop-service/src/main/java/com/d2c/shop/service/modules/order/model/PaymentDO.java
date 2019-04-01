package com.d2c.shop.service.modules.order.model;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("O_PAYMENT")
@ApiModel(description = "支付表")
public class PaymentDO extends BaseDelDO {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "订单号")
    private String orderSn;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "支付场景")
    private String tradeType;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "支付方式")
    private String paymentType;
    @ApiModelProperty(value = "支付流水")
    private String paymentSn;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "预付单ID")
    private String prepayId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "应用号ID")
    private String appId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商户号ID")
    private String mchId;
    @ApiModelProperty(value = "openID")
    private String openId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;
    @TableField(exist = false)
    @ApiModelProperty(value = "支付方式名")
    private String paymentTypeName;

    public String getPaymentTypeName() {
        if (StrUtil.isBlank(paymentType)) return "";
        return PaymentTypeEnum.valueOf(paymentType).getDescription();
    }

    public enum PaymentTypeEnum {
        //
        ALI_PAY("支付宝"), WX_PAY("微信支付");
        //
        private String description;

        PaymentTypeEnum(String description) {
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
