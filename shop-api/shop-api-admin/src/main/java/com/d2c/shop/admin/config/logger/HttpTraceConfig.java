package com.d2c.shop.admin.config.logger;

import com.d2c.shop.admin.config.logger.filter.HttpTraceLogFilter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author BaiCai
 */
@Configuration
@ConditionalOnWebApplication
public class HttpTraceConfig {

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ServletTraceFilterConfiguration {

        @Bean
        public HttpTraceLogFilter httpTraceLogFilter(MeterRegistry registry) {
            return new HttpTraceLogFilter(registry);
        }

    }

}
