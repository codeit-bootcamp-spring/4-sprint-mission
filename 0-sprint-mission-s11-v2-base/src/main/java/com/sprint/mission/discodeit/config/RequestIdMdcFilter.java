package com.sprint.mission.discodeit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdMdcFilter extends OncePerRequestFilter {

  private static final String REQ_ID = "requestId";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    boolean put = false;
    try {
      if (MDC.get(REQ_ID) == null) {
        MDC.put(REQ_ID, UUID.randomUUID().toString());
        put = true;
      }
      filterChain.doFilter(request, response);
    } finally {
      if (put)
        MDC.remove(REQ_ID); // 누수 방지
    }
  }
}