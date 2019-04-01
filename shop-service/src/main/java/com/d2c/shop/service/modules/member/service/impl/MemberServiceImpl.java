package com.d2c.shop.service.modules.member.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.member.mapper.MemberMapper;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.member.query.MemberQuery;
import com.d2c.shop.service.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author BaiCai
 */
@Service
public class MemberServiceImpl extends BaseService<MemberMapper, MemberDO> implements MemberService {

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Cacheable(value = "MEMBER", key = "'session:'+#account", unless = "#result == null")
    public MemberDO findByAccount(String account) {
        MemberQuery query = new MemberQuery();
        query.setAccount(account);
        MemberDO member = this.getOne(QueryUtil.buildWrapper(query));
        return member;
    }

    @Override
    @CacheEvict(value = "MEMBER", key = "'session:'+#account")
    public boolean doLogin(String account, String loginIp, String accessToken, Date accessExpired) {
        MemberDO entity = new MemberDO();
        entity.setAccessToken(new BCryptPasswordEncoder().encode(accessToken));
        entity.setAccessExpired(accessExpired);
        entity.setLoginDate(new Date());
        entity.setLoginIp(loginIp);
        MemberQuery query = new MemberQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "MEMBER", key = "'session:'+#account")
    public boolean doLogout(String account) {
        MemberDO entity = new MemberDO();
        entity.setAccessToken("");
        MemberQuery query = new MemberQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "MEMBER", key = "'session:'+#account")
    public boolean updatePassword(String account, String password) {
        MemberDO entity = new MemberDO();
        entity.setPassword(password);
        MemberQuery query = new MemberQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "MEMBER", key = "'session:'+#account")
    public int doConsume(Long id, String account, BigDecimal amount) {
        return memberMapper.doConsume(id, amount);
    }

    @Override
    public boolean updateById(MemberDO entity) {
        MemberDO member = memberMapper.selectById(entity.getId());
        redisTemplate.delete("MEMBER::session:" + member.getAccount());
        return super.updateById(entity);
    }

}
