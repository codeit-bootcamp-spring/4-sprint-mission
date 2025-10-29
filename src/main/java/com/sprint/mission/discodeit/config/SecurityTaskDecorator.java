package com.sprint.mission.discodeit.config;

import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        // 현재 스레드의 SecurityContext를 복제합니다.
        SecurityContext context = SecurityContextHolder.getContext();

        return () -> {
            try {
                // 비동기 스레드에 SecurityContext 설정
                SecurityContextHolder.setContext(context);
                runnable.run();
            } finally {
                // 비동기 작업이 끝난 후 반드시 초기화
                SecurityContextHolder.clearContext();
            }
        };
    }
}
