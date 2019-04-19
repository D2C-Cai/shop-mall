package com.d2c.shop.admin.config.security;

import com.d2c.shop.admin.config.security.authentication.UserDetailsServiceImpl;
import com.d2c.shop.admin.config.security.authorization.MyFilterSecurityInterceptor;
import com.d2c.shop.admin.config.security.handler.RestAccessDeniedHandler;
import com.d2c.shop.admin.config.security.handler.RestLogoutSuccessHandler;
import com.d2c.shop.admin.config.security.jwtfilter.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author BaiCai
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private RestAccessDeniedHandler restAccessDeniedHandler;
    @Autowired
    private RestLogoutSuccessHandler restLogoutSuccessHandler;
    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
                .authorizeRequests();
        for (String url : ignoreUrlsConfig.getUrls()) {
            registry.antMatchers(url).permitAll();
        }
        registry.and()
                // 表单登录方式
                .formLogin()
                .loginPage("/login/expired")
                .loginProcessingUrl("/back/user/login")
                .permitAll()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                // 默认登出方式
                .and()
                .logout()
                .logoutUrl("/back/user/logout")
                .permitAll()
                .logoutSuccessHandler(restLogoutSuccessHandler)
                // 任何请求需要身份认证
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                // 关闭跨站请求防护
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 自定义权限拒绝处理类
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restAccessDeniedHandler)
                // 自定义权限拦截器JWT过滤器
                .and()
                .addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class)
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), "/back/**", ignoreUrlsConfig.getUrls()));
    }

}
