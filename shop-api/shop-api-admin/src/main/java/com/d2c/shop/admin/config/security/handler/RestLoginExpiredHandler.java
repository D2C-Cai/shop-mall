package com.d2c.shop.admin.config.security.handler;

import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Cai
 */
@RestController
@RequestMapping("")
public class RestLoginExpiredHandler {

    @RequestMapping(value = "/login/expired", method = RequestMethod.GET)
    public void expired(HttpServletResponse response) {
        Response.out(response, Response.failed(ResultCode.ACCESS_DENIED));
    }

}
