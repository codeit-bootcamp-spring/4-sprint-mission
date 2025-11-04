package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    // 현재 스레드의 MDC 데이터 복사
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    // 현재 스레드의 SecurityContext를 복사
    SecurityContext context = SecurityContextHolder.getContext();

    return () -> {
      try {
        if (contextMap != null) {
          // 비동기 스레드에 MDC 컨텍스트를 설정
          MDC.setContextMap(contextMap);
          // 비동기 스레드에 SecurityContext 설정
          SecurityContextHolder.setContext(context);
        }
        runnable.run(); // 실제 비즈니스 로직 실행
      } finally {
        // 누락 방지
        MDC.clear();
        SecurityContextHolder.clearContext();
      }
    };
  }
}
