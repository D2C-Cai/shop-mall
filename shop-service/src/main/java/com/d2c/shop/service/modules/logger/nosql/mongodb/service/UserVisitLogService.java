package com.d2c.shop.service.modules.logger.nosql.mongodb.service;

import cn.hutool.core.date.DateUtil;
import com.d2c.shop.service.modules.logger.nosql.mongodb.document.UserVisitLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Cai
 */
@Service
public class UserVisitLogService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void doSaveLog(UserVisitLog log) {
        Date now = new Date();
        Criteria criteria = Criteria
                .where("shopId").is(log.getShopId())
                .and("memberId").is(log.getMemberId())
                .and("time").gte(DateUtil.beginOfDay(now));
        Query query = new Query();
        query.addCriteria(criteria);
        UserVisitLog old = mongoTemplate.findOne(query, UserVisitLog.class);
        if (old != null) {
            old.setTime(now);
            mongoTemplate.save(old);
        } else {
            mongoTemplate.insert(log);
        }
    }

    public int countDaily(Long shopId) {
        Date now = new Date();
        Criteria criteria = Criteria
                .where("shopId").is(shopId)
                .and("time").gte(DateUtil.beginOfDay(now));
        Query query = new Query();
        query.addCriteria(criteria);
        int count = (int) mongoTemplate.count(query, UserVisitLog.class);
        return count;
    }

}
