package com.d2c.shop.admin.api.security;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.security.model.UserRoleDO;
import com.d2c.shop.service.modules.security.query.UserRoleQuery;
import com.d2c.shop.service.modules.security.service.UserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author BaiCai
 */
@Api(description = "用户角色关系")
@RestController
@RequestMapping("/back/user_role")
public class UserRoleController extends BaseCtrl<UserRoleDO, UserRoleQuery> {

    @Autowired
    private UserRoleService userRoleService;

    @Override
    @ApiOperation(value = "新增数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<UserRoleDO> insert(@RequestBody UserRoleDO entity) {
        UserRoleQuery query = new UserRoleQuery();
        query.setUserId(entity.getUserId());
        query.setRoleId(entity.getRoleId());
        userRoleService.remove(QueryUtil.buildWrapper(query));
        return super.insert(entity);
    }

    @Override
    @ApiOperation(value = "通过ID获取数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/{id}", method = RequestMethod.GET)
    public R<UserRoleDO> select(@PathVariable Long id) {
        return super.select(id);
    }

    @Override
    @ApiOperation(value = "通过ID更新数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<UserRoleDO> update(@RequestBody UserRoleDO entity) {
        return super.update(entity);
    }

    @Override
    @ApiOperation(value = "通过ID删除数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R delete(Long[] ids) {
        return super.delete(ids);
    }

    @Override
    @ApiOperation(value = "分页查询数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/page", method = RequestMethod.POST)
    public R<Page<UserRoleDO>> selectPage(PageModel page, UserRoleQuery query) {
        return super.selectPage(page, query);
    }

}
