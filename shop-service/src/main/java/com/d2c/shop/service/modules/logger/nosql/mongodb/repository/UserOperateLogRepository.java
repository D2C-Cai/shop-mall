package com.d2c.shop.service.modules.logger.nosql.mongodb.repository;

import com.d2c.shop.service.modules.logger.nosql.mongodb.document.UserOperateLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Cai
 */
public interface UserOperateLogRepository extends MongoRepository<UserOperateLog, Long> {

}
