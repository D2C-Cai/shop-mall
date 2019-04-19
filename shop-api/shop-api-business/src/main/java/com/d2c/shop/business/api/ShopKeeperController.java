package com.d2c.shop.business.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.SecurityConstant;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.common.utils.RequestUtil;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.core.query.ShopkeeperQuery;
import com.d2c.shop.service.modules.core.service.ShopkeeperService;
import com.d2c.shop.service.modules.logger.service.SmsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Cai
 */
@Api(description = "店员业务")
@RestController
@RequestMapping("/b_api/shopkeeper")
public class ShopKeeperController extends BaseController {

    @Autowired
    private SmsService smsService;
    @Autowired
    private ShopkeeperService shopkeeperService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "登录信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public R<ShopkeeperDO> info() {
        return Response.restResult(loginKeeperHolder.getLoginKeeper(), ResultCode.SUCCESS);
    }

    private ShopkeeperDO login(String account) {
        Date accessExpired = DateUtil.offsetDay(new Date(), SecurityConstant.B_OFFSET_DAY);
        String accessToken = SecurityConstant.TOKEN_PREFIX + Jwts.builder()
                .setSubject(account)
                .claim(SecurityConstant.AUTHORITIES, "")
                .setExpiration(accessExpired)
                .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                .compact();
        shopkeeperService.doLogin(account, RequestUtil.getRequestIp(request), accessToken, accessExpired);
        ShopkeeperDO keeper = shopkeeperService.findByAccount(account);
        keeper.setLoginToken(accessToken);
        return keeper;
    }

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public R<ShopkeeperDO> login(String account, String password) {
        Asserts.notNull("账号和密码不能为空", account, password);
        ShopkeeperDO keeper = shopkeeperService.findByAccount(account);
        Asserts.notNull("账号不存在，请仔细检查", keeper);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, keeper.getPassword())) {
            this.securityGuard(account);
        }
        keeper = this.login(account);
        redisTemplate.delete("LOGIN_ERROR::" + account);
        Asserts.gt(keeper.getStatus(), 0, "账号未激活，请联系管理员");
        return Response.restResult(keeper, ResultCode.SUCCESS);
    }

    // 5分钟内只允许3次密码错误
    private void securityGuard(String account) {
        Object times = redisTemplate.opsForValue().get("B_LOGIN_ERROR::" + account);
        if (times == null) {
            redisTemplate.opsForValue().set("B_LOGIN_ERROR::" + account, 1, 5, TimeUnit.MINUTES);
        } else {
            Integer errorTimes = Integer.valueOf(times.toString());
            if (errorTimes >= 3) {
                throw new ApiException("您已经连续输错3次密码，请5分钟后继续尝试");
            } else {
                redisTemplate.opsForValue().set("B_LOGIN_ERROR::" + account, errorTimes + 1, 5, TimeUnit.MINUTES);
            }
        }
        throw new ApiException("密码错误，5分钟内只允许3次错误");
    }

    @ApiOperation(value = "登出")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public R logout() {
        shopkeeperService.doLogout(loginKeeperHolder.getLoginAccount());
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "店主注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public R<ShopkeeperDO> register(String account, String password, String code) {
        Asserts.eq(smsService.doCheck(account, code), true, "验证码不正确");
        Asserts.notNull("账号和密码不能为空", account, password);
        if (!Validator.isMobile(account)) {
            return Response.failed(ResultCode.FAILED, "手机号不符合规则");
        }
        ShopkeeperDO old = shopkeeperService.findByAccount(account);
        Asserts.isNull("该账号已存在", old);
        ShopkeeperDO keeper = new ShopkeeperDO();
        keeper.setAccount(account);
        keeper.setPassword(new BCryptPasswordEncoder().encode(password));
        keeper.setRegisterIp(RequestUtil.getRequestIp(request));
        keeper.setRole(ShopkeeperDO.RoleEnum.BOSS.name());
        keeper.setStatus(1);
        shopkeeperService.save(keeper);
        return Response.restResult(this.login(account), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public R password(String account, String password, String code) {
        Asserts.eq(smsService.doCheck(account, code), true, "验证码不正确");
        Asserts.notNull("账号和密码不能为空", account, password);
        ShopkeeperDO keeper = shopkeeperService.findByAccount(account);
        Asserts.notNull("账号不存在，请仔细检查", keeper);
        Asserts.gt(keeper.getStatus(), 0, "账号未激活，请联系管理员");
        shopkeeperService.updatePassword(account, new BCryptPasswordEncoder().encode(password));
        shopkeeperService.doLogout(account);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<ShopkeeperDO>> list(PageModel page) {
        ShopkeeperQuery query = new ShopkeeperQuery();
        query.setShopId(loginKeeperHolder.getLoginShopId());
        Page<ShopkeeperDO> pager = (Page<ShopkeeperDO>) shopkeeperService.page(page, QueryUtil.buildWrapper(query));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<ShopkeeperDO> select(@PathVariable Long id) {
        ShopkeeperDO keeper = shopkeeperService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, keeper);
        Asserts.eq(keeper.getShopId(), loginKeeperHolder.getLoginShopId(), "您不是本店店员");
        return Response.restResult(keeper, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "店员新增")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<ShopkeeperDO> insert(@RequestBody ShopkeeperDO keeper) {
        Asserts.notNull("账号和密码不能为空", keeper.getAccount(), keeper.getPassword());
        if (!Validator.isMobile(keeper.getAccount())) {
            return Response.failed(ResultCode.FAILED, "手机号不符合规则");
        }
        ShopkeeperDO old = shopkeeperService.findByAccount(keeper.getAccount());
        Asserts.isNull("该账号已存在", old);
        ShopkeeperDO entity = new ShopkeeperDO();
        entity.setAccount(keeper.getAccount());
        entity.setPassword(new BCryptPasswordEncoder().encode(keeper.getPassword()));
        entity.setRegisterIp(RequestUtil.getRequestIp(request));
        entity.setShopId(loginKeeperHolder.getLoginShopId());
        entity.setRole(ShopkeeperDO.RoleEnum.CLERK.name());
        entity.setStatus(keeper.getStatus());
        entity.setRemark(keeper.getRemark());
        shopkeeperService.save(entity);
        return Response.restResult(entity, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "个人信息更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<ShopkeeperDO> update(Long id, String nickname, String avatar, Integer status) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        ShopkeeperDO entity = new ShopkeeperDO();
        entity.setNickname(nickname);
        entity.setAvatar(avatar);
        entity.setStatus(status);
        entity.setId(keeper.getId());
        if (id != null) {
            ShopkeeperDO clerk = shopkeeperService.getById(id);
            Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, clerk);
            Asserts.eq(keeper.getShopId(), clerk.getShopId(), "您不是本店店员");
            entity.setId(id);
        }
        shopkeeperService.updateById(entity);
        return Response.restResult(shopkeeperService.getById(id), ResultCode.SUCCESS);
    }

}
