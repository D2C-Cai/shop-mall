package com.d2c.shop.service.common.api.exception;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.ApiConstant;
import com.d2c.shop.service.common.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cai
 */
@Slf4j
@ControllerAdvice
public class ExceptionsHandler {

    @Autowired
    public HttpServletRequest request;

    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public R handle(RuntimeException e) {
        if (e instanceof ApiException) {
            log.error(this.buildError(e.getMessage()));
            ApiException ex = (ApiException) e;
            if (ex.getErrorCode() == null) {
                return Response.failed(ResultCode.FAILED, e.getMessage());
            }
            return Response.failed(ex.getErrorCode());
        } else if (e instanceof MybatisPlusException) {
            MybatisPlusException ex = (MybatisPlusException) e;
            return Response.failed(ResultCode.SERVER_EXCEPTION, e.getMessage());
        }
        e.printStackTrace();
        return Response.failed(ResultCode.SERVER_EXCEPTION, e.getMessage());
    }

    private String buildError(String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(RequestUtil.getRequestIp(request));
        builder.append("]:[");
        builder.append(request.getHeader(ApiConstant.APP_TERMINAL));
        builder.append("/");
        builder.append(request.getHeader(ApiConstant.APP_VERSION));
        builder.append("]:[");
        builder.append(request.getRequestURI());
        builder.append(" ");
        builder.append(request.getMethod());
        builder.append("] ");
        builder.append(msg);
        return builder.toString();
    }

}
