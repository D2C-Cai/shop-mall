package com.d2c.shop.service.modules.security.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.modules.security.mapper.RoleMapper;
import com.d2c.shop.service.modules.security.model.RoleDO;
import com.d2c.shop.service.modules.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author BaiCai
 */
@Service
public class RoleServiceImpl extends BaseService<RoleMapper, RoleDO> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<RoleDO> findByUserId(Long userId) {
        return roleMapper.findByUserId(userId);
    }

    @Override
    public List<RoleDO> findByMenuId(Long menuId) {
        return roleMapper.findByMenuId(menuId);
    }

}
