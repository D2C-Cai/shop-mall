package com.d2c.shop.customer.api.base;

import com.d2c.shop.customer.api.holder.LoginMemberHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cai
 */
public abstract class BaseController {

    @Autowired
    public LoginMemberHolder loginMemberHolder;
    @Autowired
    public HttpServletRequest request;

}
