package com.d2c.shop.service.modules.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.security.model.MenuDO;

import java.util.List;

/**
 * @author BaiCai
 */
public interface MenuService extends IService<MenuDO> {

    List<MenuDO> findByRoleId(List<Long> roleIds);

}
