package com.epamlab.gymcrm.config;

import com.epamlab.gymcrm.logging.TxIdFilter;   // <- same package where you created the filter
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignTxIdConfig {

    @Bean
    public RequestInterceptor txIdPropagationInterceptor() {
        return template -> {
            String txId = MDC.get(TxIdFilter.MDC_KEY);
            if (txId != null) {
                template.header(TxIdFilter.TX_HEADER, txId);
            }
        };
    }
}
