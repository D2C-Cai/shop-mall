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

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("P_COUPON")
@ApiModel(description = "优惠券表")
public class CouponDO extends BaseDO {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "名称")
    private String name;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "类型 1,0,-1")
    private Integer type;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "拼团 1,0")
    private Integer crowd;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "满XX元")
    private BigDecimal needAmount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "减XX元")
    private BigDecimal amount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "总发行量")
    private Integer circulation;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "已领用量")
    private Integer consumption;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "单人限领")
    private Integer restriction;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "领取期限-开始")
    private Date receiveStartDate;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "领取期限-结束")
    private Date receiveEndDate;
    @ApiModelProperty(value = "使用固定期限-开始")
    private Date serviceStartDate;
    @ApiModelProperty(value = "使用固定期限-结束")
    private Date serviceEndDate;
    @ApiModelProperty(value = "使用顺延期限-小时")
    private Integer serviceSustain;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @TableField(exist = false)
    @ApiModelProperty(value = "类型名")
    private String typeName;

    public String getTypeName() {
        return TypeEnum.getName(this.getType());
    }

    public boolean available() {
        if (status == null || receiveStartDate == null || receiveEndDate == null) return false;
        Date now = new Date();
        if (status == 1 && now.after(receiveStartDate) && now.before(receiveEndDate)) return true;
        return false;
    }

    public enum TypeEnum {
        //
        ALL(0, "全部商品"),
        INCLUDE(1, "指定商品可用"),
        EXCLUDE(-1, "指定商品不可用");
        //
        private Integer code;
        private String description;

        TypeEnum(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public static TypeEnum getEnum(Integer code) {
            if (code == null) return ALL;
            for (TypeEnum s : TypeEnum.values()) {
                if (s.getCode() == code) {
                    return s;
                }
            }
            return ALL;
        }

        public static String getName(Integer code) {
            if (code == null) return "";
            for (TypeEnum s : TypeEnum.values()) {
                if (s.getCode() == code) {
                    return s.getDescription();
                }
            }
            return "";
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
