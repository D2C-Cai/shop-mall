package com.d2c.shop.service.modules.logger.nosql.elasticsearch.document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @author BaiCai
 */
@Data
@ApiModel(description = "MQ消费失败日志")
@Document(indexName = "logger-index", type = "mq_error_log")
public class MqErrorLog implements Serializable {

    @Id
    @ApiModelProperty(value = "主键(时间戳方便查询)")
    private Long id;
    @ApiModelProperty(value = "时间")
    private String time;
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "内容")
    private String message;
    @ApiModelProperty(value = "是否处理")
    private Integer status;

}
