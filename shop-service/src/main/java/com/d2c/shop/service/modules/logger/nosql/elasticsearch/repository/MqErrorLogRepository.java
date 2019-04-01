package com.d2c.shop.service.modules.logger.nosql.elasticsearch.repository;

import com.d2c.shop.service.modules.logger.nosql.elasticsearch.document.MqErrorLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author BaiCai
 */
public interface MqErrorLogRepository extends ElasticsearchRepository<MqErrorLog, Long> {

}
