package com.d2c.shop.service.modules.logger.nosql.elasticsearch.repository;

import com.d2c.shop.service.modules.logger.nosql.elasticsearch.document.BackTraceLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author BaiCai
 */
public interface BackTraceLogRepository extends ElasticsearchRepository<BackTraceLog, Long> {

}
