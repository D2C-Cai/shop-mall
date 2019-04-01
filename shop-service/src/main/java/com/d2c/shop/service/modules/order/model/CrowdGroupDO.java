package com.d2c.shop.service.modules.order.model;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.annotation.Prevent;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import com.d2c.shop.service.modules.product.model.ProductDO;
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
@TableName("O_CROWD_GROUP")
@ApiModel(description = "拼团团组表")
public class CrowdGroupDO extends BaseDO {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品ID")
    private Long productId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品名称")
    private String productName;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品图片")
    private String productPic;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品规格")
    private String standard;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品单价")
    private BigDecimal productPrice;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "实时单价")
    private BigDecimal crowdPrice;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "成团人数")
    private Integer crowdNum;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "参团人数")
    private Integer attendNum;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "付款人数")
    private Integer paidNum;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "过期时间")
    private Date deadline;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "虚拟 1,0")
    private Integer virtual;
    @ApiModelProperty(value = "头像列表")
    private String avatars;
    @TableField(exist = false)
    @ApiModelProperty(value = "拼团商品")
    private ProductDO product;
    @TableField(exist = false)
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    public boolean available() {
        if (status == null || deadline == null || attendNum == null || crowdNum == null) return false;
        Date now = new Date();
        if (status == 0 && now.before(deadline) && attendNum < crowdNum) return true;
        return false;
    }

    public String pushAvatars(Long id, String nickname, String avatar) {
        JSONArray array = new JSONArray();
        if (StrUtil.isNotBlank(avatars)) {
            array = JSONArray.parseArray(avatars);
        }
        if (array.size() < 10) {
            JSONObject object = new JSONObject();
            object.put("id", id);
            object.put("nickname", nickname);
            object.put("avatar", avatar);
            array.add(object);
        }
        return array.toJSONString();
    }

    public String popAvatars(Long id) {
        if (StrUtil.isNotBlank(avatars)) {
            JSONArray array = JSONArray.parseArray(avatars);
            Object[] popArray = array.stream().filter(c -> id.equals(((JSONObject) c).getLong("id"))).toArray();
            if (popArray.length > 0) {
                array.remove(popArray[0]);
            }
            return array.toJSONString();
        }
        return avatars;
    }

}
