package com.d2c.shop.admin.config.security.authorization;

import cn.hutool.core.util.StrUtil;
import com.d2c.shop.service.modules.security.model.MenuDO;
import com.d2c.shop.service.modules.security.model.RoleDO;
import com.d2c.shop.service.modules.security.service.MenuService;
import com.d2c.shop.service.modules.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author BaiCai
 */
@Component
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    /**
     * 内存中存储权限表中所有操作请求权限
     */
    private static Map<String, Collection<ConfigAttribute>> map = null;
    @Autowired
    private MenuService menuService;
    @Autowired
    private RoleService roleService;

    @PostConstruct
    public void loadDataSource() {
        map = new ConcurrentHashMap<>();
        List<MenuDO> menus = menuService.list();
        for (MenuDO menu : menus) {
            List<RoleDO> roles = roleService.findByMenuId(menu.getId());
            List<ConfigAttribute> configAttributes = new ArrayList<>();
            if (roles != null && roles.size() > 0) {
                roles.forEach(item -> {
                    if (StrUtil.isNotBlank(item.getCode())) {
                        configAttributes.add(new SecurityConfig(item.getCode()));
                    }
                });
            }
            map.put(menu.getPath(), configAttributes);
        }
    }

    public void clearDataSource() {
        map.clear();
        map = null;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if (map == null) this.loadDataSource();
        String url = ((FilterInvocation) o).getRequestUrl();
        PathMatcher pathMatcher = new AntPathMatcher();
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (pathMatcher.match(path, url)) {
                return map.get(path);
            }
        }
        // 未设置操作请求权限，返回空集合
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}
