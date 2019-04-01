package com.d2c.shop.service.modules.member.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.member.mapper.OauthMapper;
import com.d2c.shop.service.modules.member.model.OauthDO;
import com.d2c.shop.service.modules.member.query.OauthQuery;
import com.d2c.shop.service.modules.member.service.OauthService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author BaiCai
 */
@Service
public class OauthServiceImpl extends BaseService<OauthMapper, OauthDO> implements OauthService {

    @Override
    @Cacheable(value = "OAUTH", key = "'session:'+#unionId", unless = "#result == null")
    public OauthDO findByUnionId(String unionId) {
        OauthQuery query = new OauthQuery();
        query.setUnionId(unionId);
        return this.getOne(QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "OAUTH", key = "'session:'+#unionId")
    public boolean doLogin(String unionId, String nickname, String avatar, String accessToken, Date accessExpired) {
        OauthDO entity = new OauthDO();
        entity.setNickname(nickname);
        entity.setAvatar(avatar);
        entity.setAccessToken(new BCryptPasswordEncoder().encode(accessToken));
        entity.setAccessExpired(accessExpired);
        OauthQuery query = new OauthQuery();
        query.setUnionId(unionId);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

}
