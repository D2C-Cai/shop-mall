package com.d2c.shop.business.api.holder;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.SecurityConstant;
import com.d2c.shop.service.common.utils.SpringUtil;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.core.service.ShopkeeperService;
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
public class LoginKeeperHolder {

    @Autowired
    private HttpServletRequest request;

    public ShopkeeperDO getLoginKeeper() {
        String accessToken = request.getHeader(SecurityConstant.ACCESS_TOKEN);
        if (StrUtil.isBlank(accessToken)) throw new ApiException(ResultCode.LOGIN_EXPIRED);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                    .parseClaimsJws(accessToken.replace(SecurityConstant.TOKEN_PREFIX, ""))
                    .getBody();
            String account = claims.getSubject();
            // Redis获取用户session
            ShopkeeperDO keeper = SpringUtil.getBean(ShopkeeperService.class).findByAccount(account);
            Asserts.notNull(ResultCode.LOGIN_EXPIRED, keeper);
            // 验证token是否一致
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(accessToken, keeper.getAccessToken())) {
                throw new ApiException(ResultCode.LOGIN_EXPIRED);
            }
            // 验证token是否过期
            Date expireDate = claims.getExpiration();
            if (expireDate.before(keeper.getAccessExpired())) {
                throw new ApiException(ResultCode.LOGIN_EXPIRED);
            }
            return keeper;
        } catch (JwtException e) {
            throw new ApiException(ResultCode.LOGIN_EXPIRED);
        }
    }

    public Long getLoginShopId() {
        return this.getLoginKeeper().getShopId();
    }

    public String getLoginAccount() {
        return this.getLoginKeeper().getAccount();
    }

}
