package com.d2c.shop.service.modules.security.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.shop.service.common.api.annotation.Assert;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.emuns.AssertEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("SYS_MENU")
@ApiModel(description = "菜单表")
public class MenuDO extends BaseDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "名称")
    private String name;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "Ant路径")
    private String path;
    @ApiModelProperty(value = "前端路由")
    private String route;
    @ApiModelProperty(value = "前端图标")
    private String logo;
    @ApiModelProperty(value = "前端组件")
    private String assembly;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "父级ID")
    private Long parentId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @TableField(exist = false)
    @ApiModelProperty(value = "子级列表")
    private List<MenuDO> children = new ArrayList<>();

    public enum TypeEnum {
        DIR, MENU, BUTTON
    }

}
