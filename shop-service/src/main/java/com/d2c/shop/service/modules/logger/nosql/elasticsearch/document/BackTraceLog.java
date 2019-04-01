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
@ApiModel(description = "后台操作日志")
@Document(indexName = "logger-index", type = "back_trace_log")
public class BackTraceLog implements Serializable {

    @Id
    @ApiModelProperty(value = "主键(时间戳方便查询)")
    private Long id;
    @ApiModelProperty(value = "IP地址")
    private String ip;
    @ApiModelProperty(value = "请求时间")
    private String time;
    @ApiModelProperty(value = "请求地址")
    private String path;
    @ApiModelProperty(value = "请求方式")
    private String method;
    @ApiModelProperty(value = "请求响应")
    private Integer status;
    @ApiModelProperty(value = "请求账号")
    private String username;
    @ApiModelProperty(value = "请求耗时")
    private Long timeTaken;
    @ApiModelProperty(value = "请求参数")
    private String parameterMap;
    @ApiModelProperty(value = "请求参数")
    private String requestBody;
    @ApiModelProperty(value = "请求返回")
    private String responseBody;

}
