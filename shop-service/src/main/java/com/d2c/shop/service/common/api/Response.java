package com.d2c.shop.service.common.api;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;

/**
 * @author BaiCai
 */
@Slf4j
@ApiModel(description = "返回结果")
public final class Response<T> extends R<T> {

    public static R failed(IErrorCode errorCode, String msg) {
        R result = failed(errorCode);
        result.setMsg(msg);
        return result;
    }

    public static void out(ServletResponse response, R result) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.println(JSONUtil.parse(result).toJSONString(0));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

}
