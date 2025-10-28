package com.sprint.mission.discodeit.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public TaskDecorator mdcSecurityContextTaskDecorator() {
        return (Runnable task) -> {
            // 부모 스레드의 MDC와 SecurityContext를 캡쳐
            final Map<String, String> parentMdc = MDC.getCopyOfContextMap();
            final SecurityContext parentSecurityContext = SecurityContextHolder.getContext();

            return () -> {
                // 이전 상태 백업(중첩/재진입 대비)
                final Map<String, String> previousMdc = MDC.getCopyOfContextMap();
                final SecurityContext previousSecurityContext = SecurityContextHolder.getContext();

                try {
                    // MDC 전파
                    if (parentMdc != null) {
                        MDC.setContextMap(parentMdc);
                    } else {
                        MDC.clear();
                    }

                    // SecurityContext 전파
                    SecurityContextHolder.setContext(parentSecurityContext);

                    // 실제 작업 실행
                    task.run();
                } finally {
                    // 실행 전 상태로 복구 + 누수 방지
                    if (previousMdc != null) {
                        MDC.setContextMap(previousMdc);
                    } else {
                        MDC.clear();
                    }
                    SecurityContextHolder.setContext(previousSecurityContext);
                }
            };
        };
    }

    @Bean(name = {"taskExecutor", "applicationTaskExecutor"})
    public Executor taskExecutor(TaskDecorator mdcSecurityContextTaskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 운영 환경에 맞게 조정하세요.
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("async-");
        executor.setTaskDecorator(mdcSecurityContextTaskDecorator);
        executor.initialize();
        return executor;
    }
}
