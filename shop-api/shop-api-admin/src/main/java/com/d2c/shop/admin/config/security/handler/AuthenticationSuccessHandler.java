package com.d2c.shop.admin.config.security.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.d2c.shop.admin.config.security.authorization.MySecurityMetadataSource;
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
import org.springframework.beans.BeanUtils;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
            List<MenuDO> mine = menuService.findByRoleId(roleId);
            List<MenuDO> dirs = this.assembleMenus(mine);
            user.setMenus(dirs);
        }
        Response.out(response, Response.restResult(user, ResultCode.SUCCESS));
    }

    private List<MenuDO> assembleMenus(List<MenuDO> mine) {
        List<MenuDO> dirs = new ArrayList<>();
        List<MenuDO> menus = new ArrayList<>();
        List<MenuDO> buttons = new ArrayList<>();
        Map<Long, MenuDO> dirMap = new ConcurrentHashMap<>();
        Map<Long, MenuDO> menuMap = new ConcurrentHashMap<>();
        for (MenuDO item : mine) {
            if (item.getType().equals(MenuDO.TypeEnum.MENU.name())) {
                menus.add(item);
                menuMap.put(item.getId(), item);
                // 加入父级的DIR
                MenuDO parentDir = MySecurityMetadataSource.all.get(item.getParentId());
                if (parentDir != null && dirMap.get(parentDir.getId()) == null) {
                    MenuDO temp = ObjectUtil.cloneByStream(parentDir);
                    dirs.add(temp);
                    dirMap.put(temp.getId(), temp);
                }
            }
        }
        for (MenuDO item : mine) {
            if (item.getType().equals(MenuDO.TypeEnum.BUTTON.name())) {
                buttons.add(item);
                // 加入父级的MENU
                MenuDO parentMenu = MySecurityMetadataSource.all.get(item.getParentId());
                if (parentMenu != null && menuMap.get(parentMenu.getId()) == null) {
                    MenuDO temp1 = ObjectUtil.cloneByStream(parentMenu);
                    menus.add(temp1);
                    menuMap.put(temp1.getId(), temp1);
                    // 加入父级的DIR
                    MenuDO parentDir = MySecurityMetadataSource.all.get(parentMenu.getParentId());
                    if (parentDir != null && dirMap.get(parentDir.getId()) == null) {
                        MenuDO temp2 = ObjectUtil.cloneByStream(parentDir);
                        BeanUtils.copyProperties(parentDir, temp2);
                        dirs.add(temp2);
                        dirMap.put(temp2.getId(), temp2);
                    }
                }
            }
        }
        Collections.sort(dirs, (o1, o2) -> (o2.getSort()).compareTo(o1.getSort()));
        Collections.sort(menus, (o1, o2) -> (o2.getSort()).compareTo(o1.getSort()));
        Collections.sort(buttons, (o1, o2) -> (o2.getSort()).compareTo(o1.getSort()));
        for (MenuDO menu : menus) {
            if (menu.getParentId() != null && dirMap.get(menu.getParentId()) != null) {
                dirMap.get(menu.getParentId()).getChildren().add(menu);
            }
        }
        for (MenuDO button : buttons) {
            if (button.getParentId() != null && menuMap.get(button.getParentId()) != null) {
                menuMap.get(button.getParentId()).getChildren().add(button);
            }
        }
        return dirs;
    }

}
