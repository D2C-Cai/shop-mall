package com.d2c.shop.admin.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiCai
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ignored")
public class IgnoreUrlsConfig {

    private List<String> urls = new ArrayList<>();

}
