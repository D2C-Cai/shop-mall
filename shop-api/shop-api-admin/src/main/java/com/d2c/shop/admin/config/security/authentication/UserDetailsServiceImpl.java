package com.d2c.shop.admin.config.security.authentication;

import com.d2c.shop.service.modules.security.model.UserDO;
import com.d2c.shop.service.modules.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author BaiCai
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDO user = userService.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("该账号不存在，请重新确认");
        }
        return new SecurityUserDetails(user);
    }

}
