package com.sprint.mission.discodeit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // RequestID 발급
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        // 민감정보 마스킹 예시 (아주 단순화)
        String maskedQuery = (query == null) ? "" : query.replaceAll("(?i)(password|pwd)=([^&]+)", "$1=****");

        log.info("[REQ][{}] {} {}{}", requestId, method, uri, (maskedQuery.isEmpty() ? "" : "?" + maskedQuery));

        try {
            filterChain.doFilter(request, response);
        } finally {
            long took = System.currentTimeMillis() - start;
            int status = response.getStatus();
            log.info("[RES][{}] status={} took={}ms", requestId, status, took);
            MDC.clear();
        }
    }
}
