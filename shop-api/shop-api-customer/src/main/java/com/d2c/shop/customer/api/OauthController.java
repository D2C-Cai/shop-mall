package com.d2c.shop.customer.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.shop.customer.api.base.BaseController;
import com.d2c.shop.customer.sdk.oauth.WechatClient;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.SecurityConstant;
import com.d2c.shop.service.common.utils.RequestUtil;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.member.model.OauthDO;
import com.d2c.shop.service.modules.member.service.MemberService;
import com.d2c.shop.service.modules.member.service.OauthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Cai
 */
@Api(description = "第三方授权业务")
@RestController
@RequestMapping("/c_api/oauth")
public class OauthController extends BaseController {

    @Autowired
    private OauthService oauthService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private WechatClient wechatClient;

    @ApiOperation(value = "授权信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public R<OauthDO> info() {
        String oauthToken = request.getHeader(SecurityConstant.OAUTH_TOKEN);
        if (StrUtil.isBlank(oauthToken)) throw new ApiException(ResultCode.LOGIN_EXPIRED);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                    .parseClaimsJws(oauthToken.replace(SecurityConstant.TOKEN_PREFIX, ""))
                    .getBody();
            String unionid = claims.getSubject();
            OauthDO oauth = oauthService.findByUnionId(unionid);
            Asserts.notNull(ResultCode.LOGIN_EXPIRED, oauth);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(oauthToken, oauth.getAccessToken())) {
                throw new ApiException(ResultCode.LOGIN_EXPIRED);
            }
            return Response.restResult(oauth, ResultCode.SUCCESS);
        } catch (JwtException e) {
            throw new ApiException(ResultCode.LOGIN_EXPIRED);
        }
    }

    @ApiOperation(value = "授权入口")
    @RequestMapping(value = "/{channel}", method = RequestMethod.GET)
    public R<OauthDO> oauth(@PathVariable String channel, String code) {
        OauthDO.ChannelEnum channelEnum = OauthDO.ChannelEnum.valueOf(channel);
        switch (channelEnum) {
            case WECHAT:
                JSONObject tokenJSON = JSONObject.parseObject(wechatClient.getAccessToken(code));
                if (tokenJSON.get("errcode") != null) {
                    throw new ApiException(String.valueOf(tokenJSON.get("errmsg")));
                }
                JSONObject userJSON = JSONObject.parseObject(wechatClient.getUserInfo(tokenJSON.getString("access_token"), tokenJSON.getString("openid")));
                if (userJSON.get("errcode") != null) {
                    throw new ApiException(String.valueOf(userJSON.get("errmsg")));
                }
                String unionid = userJSON.getString("unionid");
                String openid = userJSON.getString("openid");
                String nickname = userJSON.getString("nickname");
                String avatar = userJSON.getString("headimgurl");
                return this.login(channelEnum, unionid, openid, nickname, avatar);
            case QQ:
                break;
            default:
                break;
        }
        return Response.restResult(null, ResultCode.RESPONSE_DATA_NULL);
    }

    private R<OauthDO> login(OauthDO.ChannelEnum channel, String unionid, String openid, String nickname, String avatar) {
        Date oauthExpired = DateUtil.offsetDay(new Date(), SecurityConstant.OAUTH_OFFSET_DAY);
        String oauthToken = SecurityConstant.TOKEN_PREFIX + Jwts.builder()
                .setSubject(unionid)
                .claim(SecurityConstant.AUTHORITIES, "")
                .setExpiration(oauthExpired)
                .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                .compact();
        OauthDO oauth = oauthService.findByUnionId(unionid);
        if (oauth == null) {
            oauth = new OauthDO();
            oauth.setChannel(channel.name());
            oauth.setUnionId(unionid);
            oauth.setOpenId(openid);
            oauth.setNickname(nickname);
            oauth.setAvatar(avatar);
            oauth.setOauthToken(oauthToken);
            oauth.setAccessToken(new BCryptPasswordEncoder().encode(oauthToken));
            oauth.setAccessExpired(oauthExpired);
            oauthService.save(oauth);
        } else {
            oauthService.doLogin(unionid, nickname, avatar, oauthToken, oauthExpired);
            oauth = oauthService.findByUnionId(unionid);
            oauth.setOauthToken(oauthToken);
            if (StrUtil.isNotBlank(oauth.getAccount())) {
                String account = oauth.getAccount();
                MemberDO member = memberService.findByAccount(account);
                Asserts.gt(member.getStatus(), 0, "账号被封禁，请联系管理员");
                String accessToken = SecurityConstant.TOKEN_PREFIX + Jwts.builder()
                        .setSubject(account)
                        .claim(SecurityConstant.AUTHORITIES, "")
                        .setExpiration(oauthExpired)
                        .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                        .compact();
                memberService.doLogin(account, RequestUtil.getRequestIp(request), accessToken, oauthExpired);
                member = memberService.findByAccount(account);
                member.setLoginToken(accessToken);
                oauth.setMember(member);
            }
        }
        return Response.restResult(oauth, ResultCode.SUCCESS);
    }

}
