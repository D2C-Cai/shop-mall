package com.d2c.shop.service.modules.logger.nosql.mongodb.document;

import cn.hutool.core.util.StrUtil;
import com.d2c.shop.service.modules.logger.nosql.mongodb.support.IUoLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Cai
 */
@Data
@ApiModel(description = "用户操作日志")
@CompoundIndexes({@CompoundIndex(name = "idx_uo_daily", def = "{'type':1, 'target':1, 'targetId':1, 'memberId':1, 'shopId':1}")})
@Document(collection = "user_operate_log")
public class UserOperateLog implements IUoLog, Serializable {

    @ApiModelProperty(value = "操作类型")
    private String type;
    @ApiModelProperty(value = "操作目标")
    private String target;
    @ApiModelProperty(value = "操作目标ID")
    private Long targetId;
    @ApiModelProperty(value = "显示图片")
    private String pic;
    @ApiModelProperty(value = "显示名称")
    private String name;
    @ApiModelProperty(value = "显示备注")
    private String info;
    @ApiModelProperty(value = "操作次数")
    private Integer times;
    @Id
    @ApiModelProperty(value = "全局主键")
    private String id;
    @Indexed(name = "idx_time")
    @ApiModelProperty(value = "操作时间")
    private Date time;
    @Indexed(name = "idx_member_id")
    @ApiModelProperty(value = "操作账号ID")
    private Long memberId;
    @Indexed(name = "idx_shop_id")
    @ApiModelProperty(value = "操作店铺ID")
    private Long shopId;
    @Transient
    @ApiModelProperty(value = "操作类型名")
    private String typeName;
    @Transient
    @ApiModelProperty(value = "操作目标名")
    private String targetName;

    public UserOperateLog() {
    }

    public UserOperateLog(TypeEnum type, TargetEnum target, IUoLog entity) {
        this.time = new Date();
        this.type = type.name();
        this.target = target.name();
        this.targetId = entity.getTargetId();
        this.shopId = entity.getShopId();
        this.pic = entity.getPic();
        this.name = entity.getName();
        this.info = entity.getInfo();
        this.times = 1;
    }

    public String getTypeName() {
        if (StrUtil.isBlank(type)) return "";
        return TypeEnum.valueOf(type).getDescription();
    }

    public String getTargetName() {
        if (StrUtil.isBlank(target)) return "";
        return TargetEnum.valueOf(target).getDescription();
    }

    public enum TypeEnum {
        //
        BROWSE("浏览"), CART("加购");
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

    public enum TargetEnum {
        //
        PRODUCT("商品");
        //
        private String description;

        TargetEnum(String description) {
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
