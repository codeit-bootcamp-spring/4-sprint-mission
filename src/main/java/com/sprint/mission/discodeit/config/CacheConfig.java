package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  public static final String CHANNELS_BY_USER = "channels:byUser";
  public static final String NOTIFICATIONS_BY_USER = "notifications:byUser";
  public static final String USERS_ALL = "users:all";

  @Bean
  public CacheManager cacheManager() {
    // 기본 Caffeine 빌더 (공통 정책)
    Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
        .maximumSize(100_000)                // 최대 10만 개까지 저장
        .expireAfterWrite(Duration.ofMinutes(30))  // 쓰기 후 30분 후 만료
        .expireAfterAccess(Duration.ofMinutes(10)) // 접근 없으면 10분 후 만료
        .recordStats();                      // 캐시 통계 기록 (Actuator 등에서 활용 가능)

    // 캐시별로 개별 정책을 지정하고 싶다면 new CaffeineCache()로 각각 구성
    CaffeineCache channelsCache = new CaffeineCache(CHANNELS_BY_USER, caffeineBuilder.build());
    CaffeineCache notificationsCache = new CaffeineCache(NOTIFICATIONS_BY_USER, caffeineBuilder.build());
    CaffeineCache usersCache = new CaffeineCache(USERS_ALL, caffeineBuilder.build());

    // CacheManager 등록
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(List.of(channelsCache, notificationsCache, usersCache));
    return cacheManager;
  }
}
