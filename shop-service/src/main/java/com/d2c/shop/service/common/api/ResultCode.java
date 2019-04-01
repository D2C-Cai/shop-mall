package com.d2c.shop.service.common.api;

import com.baomidou.mybatisplus.extension.api.IErrorCode;

/**
 * @author BaiCai
 */
public enum ResultCode implements IErrorCode {
    //
    SUCCESS(1, "操作成功"),
    FAILED(-1, "操作失败"),
    LOGIN_EXPIRED(-401, "登录已经过期"),
    ACCESS_DENIED(-403, "没有访问权限"),
    SERVER_EXCEPTION(-500, "服务端异常"),
    REQUEST_PARAM_NULL(-501, "请求参数为空"),
    RESPONSE_DATA_NULL(-502, "返回数据为空");
    //
    private long code;
    private String msg;

    private ResultCode(long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public long getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
