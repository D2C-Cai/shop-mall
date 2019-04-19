package com.d2c.shop.customer.api.holder;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.SecurityConstant;
import com.d2c.shop.service.common.utils.SpringUtil;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author Cai
 */
@Controller
public class LoginMemberHolder {

    @Autowired
    private HttpServletRequest request;

    public MemberDO getLoginMember() {
        String accessToken = request.getHeader(SecurityConstant.ACCESS_TOKEN);
        if (StrUtil.isBlank(accessToken)) throw new ApiException(ResultCode.LOGIN_EXPIRED);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                    .parseClaimsJws(accessToken.replace(SecurityConstant.TOKEN_PREFIX, ""))
                    .getBody();
            String account = claims.getSubject();
            // Redis获取用户session
            MemberDO member = SpringUtil.getBean(MemberService.class).findByAccount(account);
            Asserts.notNull(ResultCode.LOGIN_EXPIRED, member);
            // 验证token是否一致
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(accessToken, member.getAccessToken())) {
                throw new ApiException(ResultCode.LOGIN_EXPIRED);
            }
            // 验证token是否过期
            Date expireDate = claims.getExpiration();
            if (expireDate.before(member.getAccessExpired())) {
                throw new ApiException(ResultCode.LOGIN_EXPIRED);
            }
            return member;
        } catch (JwtException e) {
            throw new ApiException(ResultCode.LOGIN_EXPIRED);
        }
    }

    public Long getLoginId() {
        return this.getLoginMember().getId();
    }

    public String getLoginAccount() {
        return this.getLoginMember().getAccount();
    }

}
