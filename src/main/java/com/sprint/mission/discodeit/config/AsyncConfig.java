package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.decorator.MdcTaskDecorator;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//@EnableAsync
@Configuration
public class AsyncConfig {

    private ThreadPoolTaskExecutor taskExecutor(int core, int max, int queue, int keepAlive, String prefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queue);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(prefix + "-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(20);
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();

        return executor;
    }
}
