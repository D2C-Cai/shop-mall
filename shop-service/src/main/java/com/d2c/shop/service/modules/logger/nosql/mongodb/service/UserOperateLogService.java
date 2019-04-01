package com.d2c.shop.service.modules.logger.nosql.mongodb.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.modules.logger.nosql.mongodb.document.UserOperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Cai
 */
@Service
public class UserOperateLogService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void doSaveLog(UserOperateLog log) {
        Date now = new Date();
        Criteria criteria = Criteria
                .where("type").is(log.getType())
                .and("target").is(log.getTarget())
                .and("targetId").is(log.getTargetId())
                .and("memberId").is(log.getMemberId())
                .and("shopId").is(log.getShopId())
                .and("time").gte(DateUtil.beginOfDay(now));
        Query query = new Query();
        query.addCriteria(criteria);
        UserOperateLog old = mongoTemplate.findOne(query, UserOperateLog.class);
        if (old != null) {
            old.setTime(now);
            old.setTimes(old.getTimes() + 1);
            mongoTemplate.save(old);
        } else {
            mongoTemplate.insert(log);
        }
    }

    public Page<UserOperateLog> findPage(PageModel page, Long memberId, Long shopId) {
        Query query = Query.query(Criteria
                .where("memberId").is(memberId)
                .and("shopId").is(shopId));
        Pageable pageable = PageRequest.of((int) page.getCurrent() - 1, (int) page.getSize());
        query.with(pageable);
        query.with(new Sort(Sort.Direction.DESC, "time"));
        List<UserOperateLog> list = mongoTemplate.find(query, UserOperateLog.class);
        int count = (int) mongoTemplate.count(query, UserOperateLog.class);
        PageImpl<UserOperateLog> result = (PageImpl<UserOperateLog>) PageableExecutionUtils.getPage(list, pageable, () -> count);
        Page<UserOperateLog> pager = new Page<>();
        pager.setCurrent(page.getCurrent());
        pager.setSize(page.getSize());
        pager.setPages(result.getTotalPages());
        pager.setTotal(result.getTotalElements());
        pager.setRecords(result.getContent());
        return pager;
    }

}
