package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.decorator.CompositeTaskDecorator;
import com.sprint.mission.discodeit.config.decorator.MdcTaskDecorator;
import com.sprint.mission.discodeit.config.decorator.SecurityContextTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("eventTask-");
        executor.setTaskDecorator(new CompositeTaskDecorator(
                Arrays.asList(
                        new MdcTaskDecorator(),
                        new SecurityContextTaskDecorator()
                )
        ));
        executor.initialize();
        return executor;
    }
}
