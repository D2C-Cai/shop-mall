package com.d2c.shop.service.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author BaiCai
 */
@Configuration
@MapperScan("com.d2c.shop.service.modules.*.mapper")
public class MapperConfig {

}
