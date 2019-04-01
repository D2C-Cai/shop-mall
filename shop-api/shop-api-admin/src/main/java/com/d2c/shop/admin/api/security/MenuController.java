package com.d2c.shop.admin.api.security;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.admin.config.security.authorization.MySecurityMetadataSource;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.security.model.MenuDO;
import com.d2c.shop.service.modules.security.query.MenuQuery;
import com.d2c.shop.service.modules.security.query.RoleMenuQuery;
import com.d2c.shop.service.modules.security.service.MenuService;
import com.d2c.shop.service.modules.security.service.RoleMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author BaiCai
 */
@Api(description = "菜单管理")
@RestController
@RequestMapping("/back/menu")
public class MenuController extends BaseCtrl<MenuDO, MenuQuery> {

    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private MySecurityMetadataSource mySecurityMetadataSource;

    @Override
    @ApiOperation(value = "全部菜单")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/page", method = RequestMethod.POST)
    public R<Page<MenuDO>> selectPage(PageModel page, MenuQuery query) {
        page.setPs(PageModel.MAX_SIZE);
        page.setDesc("sort", "create_date");
        Page<MenuDO> pager = (Page<MenuDO>) menuService.page(page, QueryUtil.buildWrapper(query, false));
        List<MenuDO> menus = MenuDO.gradeList(pager.getRecords());
        pager.getRecords().clear();
        pager.getRecords().addAll(menus);
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @Override
    @ApiOperation(value = "新增数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<MenuDO> insert(@RequestBody MenuDO entity) {
        MenuQuery query = new MenuQuery();
        query.setPath(entity.getPath());
        MenuDO old = menuService.getOne(QueryUtil.buildWrapper(query));
        Asserts.isNull("Ant型的路径表达式不能重复", old);
        mySecurityMetadataSource.clearDataSource();
        return super.insert(entity);
    }

    @Override
    @ApiOperation(value = "通过ID获取数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/{id}", method = RequestMethod.GET)
    public R<MenuDO> select(@PathVariable Long id) {
        return super.select(id);
    }

    @Override
    @ApiOperation(value = "通过ID更新数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<MenuDO> update(@RequestBody MenuDO entity) {
        MenuQuery query = new MenuQuery();
        query.setPath(entity.getPath());
        List<MenuDO> old = menuService.list(QueryUtil.buildWrapper(query));
        Asserts.ge(1, old.size(), "Ant型的路径表达式不能重复");
        mySecurityMetadataSource.clearDataSource();
        return super.update(entity);
    }

    @Override
    @ApiOperation(value = "通过ID删除数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R delete(Long[] ids) {
        RoleMenuQuery query = new RoleMenuQuery();
        query.setMenuIds(ids);
        roleMenuService.remove(QueryUtil.buildWrapper(query));
        mySecurityMetadataSource.clearDataSource();
        return super.delete(ids);
    }

}
