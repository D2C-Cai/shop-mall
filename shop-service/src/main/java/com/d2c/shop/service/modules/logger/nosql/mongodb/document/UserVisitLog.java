package com.d2c.shop.service.modules.logger.nosql.mongodb.document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Cai
 */
@Data
@ApiModel(description = "用户访问日志")
@CompoundIndexes({@CompoundIndex(name = "idx_uv_daily", def = "{'shopId':1, 'memberId':1}")})
@Document(collection = "user_visit_log")
public class UserVisitLog implements Serializable {

    @Indexed(name = "idx_shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;
    @ApiModelProperty(value = "账号ID")
    private Long memberId;
    @Indexed(name = "idx_time")
    @ApiModelProperty(value = "访问时间")
    private Date time;
    @Id
    @ApiModelProperty(value = "全局主键")
    private String id;

    public UserVisitLog() {
    }

    public UserVisitLog(Long memberId, Long shopId) {
        this.time = new Date();
        this.memberId = memberId;
        this.shopId = shopId;
    }

}
