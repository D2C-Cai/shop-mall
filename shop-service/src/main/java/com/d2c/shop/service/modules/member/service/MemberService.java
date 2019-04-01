package com.d2c.shop.service.modules.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.member.model.MemberDO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author BaiCai
 */
public interface MemberService extends IService<MemberDO> {

    MemberDO findByAccount(String account);

    boolean doLogin(String account, String loginIp, String accessToken, Date accessExpired);

    boolean doLogout(String account);

    boolean updatePassword(String account, String password);

    int doConsume(Long id, String account, BigDecimal amount);

}
