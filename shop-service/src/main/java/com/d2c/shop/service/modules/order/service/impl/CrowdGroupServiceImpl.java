package com.d2c.shop.service.modules.order.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.common.utils.ExecutorUtil;
import com.d2c.shop.service.modules.order.mapper.CrowdGroupMapper;
import com.d2c.shop.service.modules.order.model.CrowdGroupDO;
import com.d2c.shop.service.modules.order.service.CrowdGroupService;
import com.d2c.shop.service.rabbitmq.sender.CrowdDelayedSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiCai
 */
@Service
public class CrowdGroupServiceImpl extends BaseService<CrowdGroupMapper, CrowdGroupDO> implements CrowdGroupService {

    @Autowired
    private CrowdGroupMapper crowdGroupMapper;
    @Autowired
    private CrowdDelayedSender crowdDelayedSender;

    @Override
    @Transactional
    public boolean save(CrowdGroupDO entity) {
        boolean success = super.save(entity);
        // 发送延迟消息
        ExecutorUtil.fixedPool.submit(() -> {
                    crowdDelayedSender.send(String.valueOf(entity.getId()), DateUtil.between(entity.getDeadline(), entity.getCreateDate(), DateUnit.SECOND));
                }
        );
        return success;
    }

    @Override
    @Transactional
    public int doAttend(Long id, String avatars) {
        return crowdGroupMapper.doAttend(id, avatars);
    }

    @Override
    @Transactional
    public int doCancel(Long id, String avatars) {
        return crowdGroupMapper.doCancel(id, avatars);
    }

}
