package com.ecommerce.order_service.config;

import feign.RequestInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ecommerce.order_service.constant.AppConstant.CORRELATION_ID;

@Configuration
public class FeignConfiguration {
    @Bean
    public RequestInterceptor correlationIdInterceptor() {
        return requestTemplate -> {
            String correlationId = MDC.get(CORRELATION_ID);

            if (StringUtils.isNotEmpty(correlationId)) {
                requestTemplate.header(CORRELATION_ID, correlationId);
            }
        };
    }
}
