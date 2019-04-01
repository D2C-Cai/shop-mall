package com.d2c.shop.service.modules.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.shop.service.modules.security.model.MenuDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiCai
 */
public interface MenuMapper extends BaseMapper<MenuDO> {

    List<MenuDO> findByRoleId(@Param("roleIds") List<Long> roleIds);

}
