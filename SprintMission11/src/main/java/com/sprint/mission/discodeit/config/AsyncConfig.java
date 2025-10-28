package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "eventTaskExecutor")
  public Executor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);     // 동시에 실행할 스레드 수
    executor.setMaxPoolSize(8);      // 최대 스레드 수
    executor.setQueueCapacity(100);  // 큐 크기
    executor.setThreadNamePrefix("event-async-"); // 로그 보기 좋게
    executor.initialize();
    return executor;
  }
}
