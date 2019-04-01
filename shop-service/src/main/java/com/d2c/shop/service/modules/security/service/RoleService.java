package com.d2c.shop.service.modules.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.security.model.RoleDO;

import java.util.List;

/**
 * @author BaiCai
 */
public interface RoleService extends IService<RoleDO> {

    List<RoleDO> findByUserId(Long userId);

    List<RoleDO> findByMenuId(Long menuId);

}
