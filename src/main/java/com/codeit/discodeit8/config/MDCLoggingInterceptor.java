package com.codeit.discodeit8.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_ID = "requestId";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String requestId = UUID.randomUUID().toString();

    MDC.put(REQUEST_ID, requestId);
    MDC.put("requestUrl", request.getRequestURI());
    MDC.put("httpMethod", request.getMethod());

    // 응답 헤더에 Request ID 추가
    response.setHeader("Discodeit-Request-ID", requestId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      Exception ex) {
    MDC.clear(); // 메모리 누수 방지
  }
}
