package com.d2c.shop.admin.api.security;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.admin.config.security.authorization.MySecurityMetadataSource;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.security.model.RoleMenuDO;
import com.d2c.shop.service.modules.security.query.RoleMenuQuery;
import com.d2c.shop.service.modules.security.service.RoleMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author BaiCai
 */
@Api(description = "角色菜单关系")
@RestController
@RequestMapping("/back/role_menu")
public class RoleMenuController extends BaseCtrl<RoleMenuDO, RoleMenuQuery> {

    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private MySecurityMetadataSource mySecurityMetadataSource;

    @Override
    @ApiOperation(value = "新增数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<RoleMenuDO> insert(@RequestBody RoleMenuDO entity) {
        RoleMenuQuery query = new RoleMenuQuery();
        query.setRoleId(entity.getRoleId());
        query.setMenuId(entity.getMenuId());
        roleMenuService.remove(QueryUtil.buildWrapper(query));
        mySecurityMetadataSource.clearDataSource();
        return super.insert(entity);
    }

    @Override
    @ApiOperation(value = "通过ID获取数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/{id}", method = RequestMethod.GET)
    public R<RoleMenuDO> select(@PathVariable Long id) {
        return super.select(id);
    }

    @Override
    @ApiOperation(value = "通过ID更新数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<RoleMenuDO> update(@RequestBody RoleMenuDO entity) {
        return super.update(entity);
    }

    @Override
    @ApiOperation(value = "通过ID删除数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R delete(Long[] ids) {
        mySecurityMetadataSource.clearDataSource();
        return super.delete(ids);
    }

    @Override
    @ApiOperation(value = "分页查询数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/page", method = RequestMethod.POST)
    public R<Page<RoleMenuDO>> selectPage(PageModel page, RoleMenuQuery query) {
        return super.selectPage(page, query);
    }

}
