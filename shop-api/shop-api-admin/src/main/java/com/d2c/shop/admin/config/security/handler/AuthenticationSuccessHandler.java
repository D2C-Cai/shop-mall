package com.d2c.shop.admin.config.security.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.SecurityConstant;
import com.d2c.shop.service.common.utils.RequestUtil;
import com.d2c.shop.service.modules.security.model.MenuDO;
import com.d2c.shop.service.modules.security.model.RoleDO;
import com.d2c.shop.service.modules.security.model.UserDO;
import com.d2c.shop.service.modules.security.service.MenuService;
import com.d2c.shop.service.modules.security.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author BaiCai
 */
@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        UserDetails loginUser = (UserDetails) authentication.getPrincipal();
        String username = loginUser.getUsername();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) loginUser.getAuthorities();
        List<String> list = new ArrayList<>();
        authorities.forEach(item -> list.add(item.getAuthority()));
        // JWT登录成功生成token
        Date accessExpired = DateUtil.offsetDay(new Date(), 7);
        String accessToken = SecurityConstant.TOKEN_PREFIX + Jwts.builder()
                .setSubject(username)
                .claim(SecurityConstant.AUTHORITIES, JSONUtil.parse(list))
                .setExpiration(accessExpired)
                .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                .compact();
        userService.doLogin(username, RequestUtil.getRequestIp(request), accessToken, accessExpired);
        UserDO user = userService.findByUsername(username);
        user.setPassword(null);
        user.setAccessToken(accessToken);
        List<RoleDO> roles = user.getRoles();
        // 用户可见的菜单列表
        if (roles.size() > 0) {
            List<Long> roleId = new ArrayList<>();
            roles.forEach(item -> roleId.add(item.getId()));
            List<MenuDO> menus = menuService.findByRoleId(roleId);
            user.setMenus(MenuDO.gradeList(menus));
        }
        Response.out(response, Response.restResult(user, ResultCode.SUCCESS));
    }

}
