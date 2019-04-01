package com.d2c.shop.service.config;

import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author BaiCai
 */
@Configuration
public class FastJsonConfig {

    @PostConstruct
    public void accept() {
        ParserConfig.getGlobalInstance().addAccept("com.d2c.shop.service.modules.core.model.");
        ParserConfig.getGlobalInstance().addAccept("com.d2c.shop.service.modules.logger.model.");
        ParserConfig.getGlobalInstance().addAccept("com.d2c.shop.service.modules.member.model.");
        ParserConfig.getGlobalInstance().addAccept("com.d2c.shop.service.modules.order.model.");
        ParserConfig.getGlobalInstance().addAccept("com.d2c.shop.service.modules.product.model.");
        ParserConfig.getGlobalInstance().addAccept("com.d2c.shop.service.modules.security.model.");
    }

}
