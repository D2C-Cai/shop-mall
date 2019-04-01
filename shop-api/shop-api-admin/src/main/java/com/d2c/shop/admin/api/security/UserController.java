package com.d2c.shop.admin.api.security;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.admin.api.base.extension.BaseExcelCtrl;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.common.utils.RequestUtil;
import com.d2c.shop.service.modules.security.model.UserDO;
import com.d2c.shop.service.modules.security.query.UserQuery;
import com.d2c.shop.service.modules.security.query.UserRoleQuery;
import com.d2c.shop.service.modules.security.service.UserRoleService;
import com.d2c.shop.service.modules.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * @author BaiCai
 */
@Api(description = "用户管理")
@RestController
@RequestMapping("/back/user")
public class UserController extends BaseExcelCtrl<UserDO, UserQuery> {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;

    @ApiOperation(value = "登录过期")
    @RequestMapping(value = "/expired", method = RequestMethod.GET)
    public R expired() {
        return Response.failed(ResultCode.ACCESS_DENIED);
    }

    @Override
    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<UserDO> insert(@RequestBody UserDO user) {
        Asserts.notNull(ResultCode.REQUEST_PARAM_NULL, user.getUsername(), user.getPassword(), user.getStatus());
        UserDO old = userService.findByUsername(user.getUsername());
        Asserts.isNull("该账号已存在", old);
        user.setRegisterIp(RequestUtil.getRequestIp(request));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return super.insert(user);
    }

    @Override
    @ApiOperation(value = "用户更新")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<UserDO> update(@RequestBody UserDO user) {
        Asserts.notNull(ResultCode.REQUEST_PARAM_NULL, user);
        UserDO entity = new UserDO();
        entity.setId(user.getId());
        entity.setStatus(user.getStatus());
        return super.update(entity);
    }

    @Override
    @ApiOperation(value = "通过ID获取数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/{id}", method = RequestMethod.GET)
    public R<UserDO> select(@PathVariable Long id) {
        return super.select(id);
    }

    @Override
    @ApiOperation(value = "通过ID删除数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R delete(Long[] ids) {
        UserRoleQuery query = new UserRoleQuery();
        query.setUserIds(ids);
        userRoleService.remove(QueryUtil.buildWrapper(query));
        return super.delete(ids);
    }

    @Override
    @ApiOperation(value = "分页查询数据")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/select/page", method = RequestMethod.POST)
    public R<Page<UserDO>> selectPage(PageModel page, UserQuery query) {
        return super.selectPage(page, query);
    }

}
