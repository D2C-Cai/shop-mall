package com.d2c.shop.service.modules.security.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.security.mapper.UserMapper;
import com.d2c.shop.service.modules.security.model.RoleDO;
import com.d2c.shop.service.modules.security.model.UserDO;
import com.d2c.shop.service.modules.security.query.UserQuery;
import com.d2c.shop.service.modules.security.service.RoleService;
import com.d2c.shop.service.modules.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author BaiCai
 */
@Service
public class UserServiceImpl extends BaseService<UserMapper, UserDO> implements UserService {

    @Autowired
    private RoleService roleService;

    @Override
    @Cacheable(value = "USER", key = "'session:'+#username", unless = "#result == null")
    public UserDO findByUsername(String username) {
        UserQuery query = new UserQuery();
        query.setUsername(username);
        UserDO user = this.getOne(QueryUtil.buildWrapper(query));
        if (user == null) return null;
        List<RoleDO> roles = roleService.findByUserId(user.getId());
        user.setRoles(roles);
        return user;
    }

    @Override
    @CacheEvict(value = "USER", key = "'session:'+#username")
    public boolean doLogin(String username, String loginIp, String accessToken, Date accessExpired) {
        UserDO entity = new UserDO();
        entity.setAccessToken(new BCryptPasswordEncoder().encode(accessToken));
        entity.setAccessExpired(accessExpired);
        entity.setLoginDate(new Date());
        entity.setLoginIp(loginIp);
        UserQuery query = new UserQuery();
        query.setUsername(username);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "USER", key = "'session:'+#username")
    public boolean doLogout(String username) {
        UserDO entity = new UserDO();
        entity.setAccessToken("");
        UserQuery query = new UserQuery();
        query.setUsername(username);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

}
