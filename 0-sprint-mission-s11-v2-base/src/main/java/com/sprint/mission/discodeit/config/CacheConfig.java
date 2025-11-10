package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import java.time.Duration;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

  public static final String CHANNELS_BY_USER = "channels:byUser";
  public static final String NOTIFICATIONS_BY_USER = "notifications:byUser";
  public static final String USERS_ALL = "users:all";

  /**
   * Redis 값 직렬화/TTL/접두사 등 공통 설정
   */
  @Bean
  public RedisCacheConfiguration redisCacheConfiguration(ObjectMapper objectMapper) {
    // 필요 시 타입 정보 포함 직렬화 (다형성 객체 캐시에 필요)
    ObjectMapper redisObjectMapper = objectMapper.copy();
    redisObjectMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        DefaultTyping.EVERYTHING,
        As.PROPERTY
    );

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                org.springframework.data.redis.serializer.StringRedisSerializer.UTF_8
            )
        )
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
            )
        )
        .computePrefixWith(name -> "discodeit:" + name + "::") // cache-name prefix
        .entryTtl(Duration.ofMinutes(10))                      // 기본 TTL
        .disableCachingNullValues();
  }

  /**
   * ✅ 핵심: RedisCacheManager를 등록(이 bean이 있으면 Simple/Caffeine으로 폴백되지 않음)
   *  - 캐시별 개별 TTL 등 세부 설정도 함께 적용
   */
  @Bean
  public CacheManager cacheManager(
      RedisConnectionFactory connectionFactory,
      RedisCacheConfiguration defaultConfig
  ) {
    // 캐시별 TTL 커스터마이징 (선택)
    Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
        CHANNELS_BY_USER,   defaultConfig.entryTtl(Duration.ofMinutes(30)),
        NOTIFICATIONS_BY_USER, defaultConfig.entryTtl(Duration.ofMinutes(5)),
        USERS_ALL,          defaultConfig.entryTtl(Duration.ofMinutes(20))
    );

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(defaultConfig)
        .withInitialCacheConfigurations(cacheConfigs)
        .build();
  }
}
