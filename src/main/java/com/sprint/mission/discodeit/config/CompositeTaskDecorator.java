package com.sprint.mission.discodeit.config;

import org.springframework.core.task.TaskDecorator;

import java.util.List;

public class CompositeTaskDecorator implements TaskDecorator {

    private final List<TaskDecorator> decorators;

    public CompositeTaskDecorator(List<TaskDecorator> decorators) {
        this.decorators = decorators;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        // 데코레이터 체인을 역순으로 감싸기 (가장 먼저 등록된 데코레이터가 가장 바깥쪽)
        for (int i = decorators.size() - 1; i >= 0; i--) {
            runnable = decorators.get(i).decorate(runnable);
        }
        return runnable;
    }
}
