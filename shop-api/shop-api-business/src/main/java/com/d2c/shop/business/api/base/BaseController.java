package com.d2c.shop.business.api.base;

import com.d2c.shop.business.api.holder.LoginKeeperHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cai
 */
public abstract class BaseController {

    @Autowired
    public LoginKeeperHolder loginKeeperHolder;
    @Autowired
    public HttpServletRequest request;

}
