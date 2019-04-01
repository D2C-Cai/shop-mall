package com.d2c.shop.admin.api.security;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.admin.config.security.authorization.MySecurityMetadataSource;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.security.model.RoleDO;
import com.d2c.shop.service.modules.security.query.RoleMenuQuery;
import com.d2c.shop.service.modules.security.query.RoleQuery;
import com.d2c.shop.service.modules.security.query.UserRoleQuery;
import com.d2c.shop.service.modules.security.service.RoleMenuService;
import com.d2c.shop.service.modules.security.service.RoleService;
import com.d2c.shop.service.modules.security.service.UserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author BaiCai
 */
@Api(description = "角色管理")
@RestController
@RequestMapping("/back/role")
public class RoleController extends BaseCtrl<RoleDO, RoleQuery> {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private MySecurityMetadataSource mySecurityMetadataSource;

    @Override
    @ApiOperation(value = "新增数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<RoleDO> insert(@RequestBody RoleDO entity) {
        RoleQuery query = new RoleQuery();
        query.setCode(entity.getCode());
        RoleDO old = roleService.getOne(QueryUtil.buildWrapper(query));
        Asserts.isNull("角色代码code不能重复", old);
        mySecurityMetadataSource.clearDataSource();
        return super.insert(entity);
    }

    @Override
    @ApiOperation(value = "通过ID获取数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/{id}", method = RequestMethod.GET)
    public R<RoleDO> select(@PathVariable Long id) {
        return super.select(id);
    }

    @Override
    @ApiOperation(value = "通过ID更新数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<RoleDO> update(@RequestBody RoleDO entity) {
        RoleQuery query = new RoleQuery();
        query.setCode(entity.getCode());
        List<RoleDO> old = roleService.list(QueryUtil.buildWrapper(query));
        Asserts.ge(1, old.size(), "角色代码code不能重复");
        mySecurityMetadataSource.clearDataSource();
        return super.update(entity);
    }

    @Override
    @ApiOperation(value = "通过ID删除数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R delete(Long[] ids) {
        RoleMenuQuery query = new RoleMenuQuery();
        query.setRoleIds(ids);
        roleMenuService.remove(QueryUtil.buildWrapper(query));
        UserRoleQuery query2 = new UserRoleQuery();
        query2.setRoleIds(ids);
        userRoleService.remove(QueryUtil.buildWrapper(query2));
        mySecurityMetadataSource.clearDataSource();
        return super.delete(ids);
    }

    @Override
    @ApiOperation(value = "分页查询数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/page", method = RequestMethod.POST)
    public R<Page<RoleDO>> selectPage(PageModel page, RoleQuery query) {
        return super.selectPage(page, query);
    }

}
