package com.d2c.shop.customer.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.shop.customer.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.SecurityConstant;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.common.utils.RequestUtil;
import com.d2c.shop.service.modules.logger.service.SmsService;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.member.model.OauthDO;
import com.d2c.shop.service.modules.member.query.OauthQuery;
import com.d2c.shop.service.modules.member.service.MemberService;
import com.d2c.shop.service.modules.member.service.OauthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Cai
 */
@Api(description = "会员业务")
@RestController
@RequestMapping("/c_api/member")
public class MemberController extends BaseController {

    @Autowired
    private SmsService smsService;
    @Autowired
    private OauthService oauthService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private OauthController oauthController;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "登录信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public R<MemberDO> info() {
        return Response.restResult(loginMemberHolder.getLoginMember(), ResultCode.SUCCESS);
    }

    private MemberDO login(String account) {
        Date accessExpired = DateUtil.offsetDay(new Date(), SecurityConstant.C_OFFSET_DAY);
        String accessToken = SecurityConstant.TOKEN_PREFIX + Jwts.builder()
                .setSubject(account)
                .claim(SecurityConstant.AUTHORITIES, "")
                .setExpiration(accessExpired)
                .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                .compact();
        memberService.doLogin(account, RequestUtil.getRequestIp(request), accessToken, accessExpired);
        MemberDO member = memberService.findByAccount(account);
        member.setLoginToken(accessToken);
        return member;
    }

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public R<MemberDO> login(String account, String password) {
        Asserts.notNull("账号和密码不能为空", account, password);
        MemberDO member = memberService.findByAccount(account);
        Asserts.notNull("账号不存在，请仔细检查", member);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, member.getPassword())) {
            this.securityGuard(account);
        }
        member = this.login(account);
        redisTemplate.delete("LOGIN_ERROR::" + account);
        Asserts.gt(member.getStatus(), 0, "账号被封禁，请联系管理员");
        return Response.restResult(member, ResultCode.SUCCESS);
    }

    // 5分钟内只允许3次密码错误
    private void securityGuard(String account) {
        Object times = redisTemplate.opsForValue().get("C_LOGIN_ERROR::" + account);
        if (times == null) {
            redisTemplate.opsForValue().set("C_LOGIN_ERROR::" + account, 1, 5, TimeUnit.MINUTES);
        } else {
            Integer errorTimes = Integer.valueOf(times.toString());
            if (errorTimes >= 3) {
                throw new ApiException("您已经连续输错3次密码，请5分钟后继续尝试");
            } else {
                redisTemplate.opsForValue().set("C_LOGIN_ERROR::" + account, errorTimes + 1, 5, TimeUnit.MINUTES);
            }
        }
        throw new ApiException("密码错误，5分钟内只允许3次错误");
    }

    @ApiOperation(value = "登出")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public R logout() {
        memberService.doLogout(loginMemberHolder.getLoginAccount());
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public R<MemberDO> register(String account, String password, String code) {
        Asserts.eq(smsService.doCheck(account, code), true, "验证码不正确");
        Asserts.notNull("账号和密码不能为空", account, password);
        if (!Validator.isMobile(account)) {
            return Response.failed(ResultCode.FAILED, "手机号不符合规则");
        }
        MemberDO old = memberService.findByAccount(account);
        Asserts.isNull("该账号已存在", old);
        MemberDO member = new MemberDO();
        member.setAccount(account);
        member.setPassword(new BCryptPasswordEncoder().encode(password));
        member.setRegisterIp(RequestUtil.getRequestIp(request));
        member.setStatus(1);
        member.setConsumeTimes(0);
        member.setConsumeAmount(BigDecimal.ZERO);
        memberService.save(member);
        return Response.restResult(this.login(account), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "绑定手机")
    @RequestMapping(value = "/bind", method = RequestMethod.POST)
    public R<MemberDO> bind(String account, String code) {
        OauthDO oauthInfo = oauthController.info().getData();
        Asserts.eq(smsService.doCheck(account, code), true, "验证码不正确");
        Asserts.notNull("手机号不能为空", account);
        if (!Validator.isMobile(account)) {
            return Response.failed(ResultCode.FAILED, "手机号不符合规则");
        }
        MemberDO member = memberService.findByAccount(account);
        if (member == null) {
            String password = DigestUtil.md5Hex(account.substring(account.length() - 6));
            member = new MemberDO();
            member.setAccount(account);
            member.setPassword(new BCryptPasswordEncoder().encode(password));
            member.setNickname(oauthInfo.getNickname());
            member.setAvatar(oauthInfo.getAvatar());
            member.setRegisterIp(RequestUtil.getRequestIp(request));
            member.setStatus(1);
            member.setConsumeTimes(0);
            member.setConsumeAmount(BigDecimal.ZERO);
            memberService.save(member);
        } else {
            if (StrUtil.isBlank(member.getNickname()) && StrUtil.isBlank(member.getAvatar())) {
                MemberDO entity = new MemberDO();
                entity.setId(member.getId());
                entity.setNickname(oauthInfo.getNickname());
                entity.setAvatar(oauthInfo.getAvatar());
                memberService.updateById(entity);
            }
        }
        OauthDO entity = new OauthDO();
        entity.setAccount(account);
        OauthQuery query = new OauthQuery();
        query.setUnionId(oauthInfo.getUnionId());
        oauthService.update(entity, QueryUtil.buildWrapper(query));
        return Response.restResult(this.login(account), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public R password(String account, String password, String code) {
        Asserts.eq(smsService.doCheck(account, code), true, "验证码不正确");
        Asserts.notNull("账号和密码不能为空", account, password);
        MemberDO member = memberService.findByAccount(account);
        Asserts.notNull("账号不存在，请仔细检查", member);
        Asserts.gt(member.getStatus(), 0, "账号被封禁，请联系管理员");
        memberService.updatePassword(account, new BCryptPasswordEncoder().encode(password));
        memberService.doLogout(account);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "个人信息修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<MemberDO> update(String nickname, String avatar, String sex) {
        MemberDO member = loginMemberHolder.getLoginMember();
        MemberDO entity = new MemberDO();
        entity.setNickname(nickname);
        entity.setAvatar(avatar);
        entity.setSex(sex);
        entity.setId(member.getId());
        memberService.updateById(entity);
        return Response.restResult(memberService.getById(member.getId()), ResultCode.SUCCESS);
    }

}
