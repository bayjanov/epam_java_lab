package com.epamlab.gymcrm.config;

import com.epamlab.gymcrm.security.jwt.JwtTokenProvider;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignJwtInterceptor implements RequestInterceptor {

    private final JwtTokenProvider tokenProvider;

    public FeignJwtInterceptor(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void apply(RequestTemplate template) {
        String jwt = "Bearer " + tokenProvider.generateInternalServiceToken();
        template.header(HttpHeaders.AUTHORIZATION, jwt);
    }
}
