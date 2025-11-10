package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("Async");
    executor.setTaskDecorator(mdcAndSecurityContextDecorator());
    executor.initialize();
    return executor;
  }

  private TaskDecorator mdcAndSecurityContextDecorator() {
    SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();

    return runnable -> {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      SecurityContext context = strategy.getContext();

      return () -> {
        try {
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }
          if (context != null) {
            strategy.setContext(context);
          }
          runnable.run();
        } finally {
          MDC.clear();
          strategy.clearContext();
        }
      };
    };
  }

}
