package com.d2c.shop.service.common.api.base;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseDO extends Model {

    @TableId(value = "id", type = IdType.ID_WORKER)
    @ApiModelProperty(value = "唯一主键ID")
    private Long id;
    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
    @TableField(value = "create_man", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建用户")
    private String createMan;
    @TableField(value = "modify_date", fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改时间")
    private Date modifyDate;
    @TableField(value = "modify_man", fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改用户")
    private String modifyMan;

}
