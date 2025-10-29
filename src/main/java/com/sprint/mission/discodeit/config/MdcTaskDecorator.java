package com.sprint.mission.discodeit.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        // 현재 스레드(MDC)의 컨텍스트를 복제합니다.
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            try {
                // 비동기 스레드에 컨텍스트를 설정합니다.
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run(); // 실제 비즈니스 로직 실행
            } finally {
                MDC.clear(); // 실행 후 반드시 클리어하여 스레드 재사용 시 누락 방지
            }
        };
    }
}
