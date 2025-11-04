package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(50);
    executor.setKeepAliveSeconds(30);
    executor.setThreadNamePrefix("CustomExecutor-"); //스레드 이름 접두사 설정 → 디버깅 및 로깅 시 유용
    executor.setTaskDecorator(new CustomTaskDecorator()); // 커스텀 TaskDecorator
    executor.initialize();
    return executor;
  }

}
