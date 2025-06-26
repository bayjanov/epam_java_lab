package com.epamlab.gymcrm.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(1)
public class TxIdFilter extends OncePerRequestFilter {

    public static final String TX_HEADER = "X-TX-ID";
    public static final String MDC_KEY   = "txId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String txId = Optional.ofNullable(request.getHeader(TX_HEADER))
                .orElse(UUID.randomUUID().toString());

        MDC.put(MDC_KEY, txId);
        response.setHeader(TX_HEADER, txId);   // propagate to client
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
