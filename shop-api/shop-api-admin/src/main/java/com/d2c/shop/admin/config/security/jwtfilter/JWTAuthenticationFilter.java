package com.d2c.shop.admin.config.security.jwtfilter;

import cn.hutool.core.util.StrUtil;
import com.d2c.shop.admin.config.security.authentication.SecurityUserDetails;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.SecurityConstant;
import com.d2c.shop.service.common.utils.SpringUtil;
import com.d2c.shop.service.modules.security.model.UserDO;
import com.d2c.shop.service.modules.security.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author BaiCai
 */
@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private String FILTER_URLS;
    private List<String> IGNORE_URLS;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, String filterUrls, List<String> ignoreUrls) {
        super(authenticationManager);
        this.FILTER_URLS = filterUrls;
        this.IGNORE_URLS = ignoreUrls;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String ignoreUrl : IGNORE_URLS) {
            if (pathMatcher.match(ignoreUrl, requestURI)) {
                chain.doFilter(request, response);
                return;
            }
        }
        if (pathMatcher.match(FILTER_URLS, requestURI)) {
            String accessToken = request.getHeader(SecurityConstant.ACCESS_TOKEN);
            if (StrUtil.isBlank(accessToken)) {
                Response.out(response, Response.failed(ResultCode.LOGIN_EXPIRED));
                return;
            }
            UsernamePasswordAuthenticationToken authentication = getAuthentication(accessToken);
            if (authentication == null) {
                Response.out(response, Response.failed(ResultCode.LOGIN_EXPIRED));
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String accessToken) {
        try {
            // JWT解析token
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                    .parseClaimsJws(accessToken.replace(SecurityConstant.TOKEN_PREFIX, ""))
                    .getBody();
            String username = claims.getSubject();
            // Redis获取用户session
            UserDO user = SpringUtil.getBean(UserService.class).findByUsername(username);
            Asserts.notNull(ResultCode.LOGIN_EXPIRED, user);
            // 验证token是否一致
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(accessToken, user.getAccessToken())) {
                return null;
            }
            // 验证token是否过期
            Date expireDate = claims.getExpiration();
            if (expireDate.before(user.getAccessExpired())) {
                return null;
            }
            // 组装并返回authentication
            SecurityUserDetails securityUserDetail = new SecurityUserDetails(user);
            User principal = new User(username, "", securityUserDetail.getAuthorities());
            return new UsernamePasswordAuthenticationToken(principal, null, securityUserDetail.getAuthorities());
        } catch (ExpiredJwtException e) {
            logger.error(e.getMessage(), e);
        } catch (JwtException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
