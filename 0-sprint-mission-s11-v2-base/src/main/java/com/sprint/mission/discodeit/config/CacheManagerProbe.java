package com.sprint.mission.discodeit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

// 애플리케이션 시작 시 어떤 CacheManager가 잡혔는지 로그로 확인
@Component
@RequiredArgsConstructor
class CacheManagerProbe implements ApplicationRunner {
  private final CacheManager cacheManager;
  @Override public void run(ApplicationArguments args) {
    System.out.println(">>> CacheManager = " + cacheManager.getClass().getName());
  }
}
