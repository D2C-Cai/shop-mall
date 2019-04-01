package com.d2c.shop.service.common.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PageModel<T> extends Page<T> {

    // 最大页长
    public static final long MAX_SIZE = 500L;
    //
    @ApiModelProperty(value = "页码")
    private long p;
    @ApiModelProperty(value = "页长")
    private long ps;

    public PageModel() {
        super();
        this.setDesc("create_date");
    }

    public void setP(long p) {
        this.p = p;
        this.setCurrent(p);
    }

    public void setPs(long ps) {
        this.ps = ps;
        this.setSize(ps);
    }

}
