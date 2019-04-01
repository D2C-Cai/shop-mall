package com.d2c.shop.admin.config.security.authentication;

import cn.hutool.core.util.StrUtil;
import com.d2c.shop.service.modules.security.model.RoleDO;
import com.d2c.shop.service.modules.security.model.UserDO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author BaiCai
 */
public class SecurityUserDetails extends UserDO implements UserDetails {

    public SecurityUserDetails(UserDO user) {
        if (user != null) {
            this.setUsername(user.getUsername());
            this.setPassword(user.getPassword());
            this.setStatus(user.getStatus());
            this.setRoles(user.getRoles());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        List<RoleDO> roles = this.getRoles();
        if (roles != null && roles.size() > 0) {
            roles.forEach(item -> {
                if (StrUtil.isNotBlank(item.getCode())) {
                    authorityList.add(new SimpleGrantedAuthority(item.getCode()));
                }
            });
        }
        return authorityList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getStatus() == 1;
    }

}
