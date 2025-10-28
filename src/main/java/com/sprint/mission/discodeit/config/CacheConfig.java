package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching // Spring Cache 활성화
@RequiredArgsConstructor
public class CacheConfig {
    private final ObjectMapper objectMapper;

//    @Bean
//    public CacheManager caffeineCacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
//                "channelCache", "channelCacheByUserId",
//                "notificationCache",
//                "userCache", "userCacheAll");
//        cacheManager.setCaffeine(
//                Caffeine.newBuilder()
//                        .maximumSize(100) // 캐시 크기 제한
//                        .expireAfterAccess(600, TimeUnit.SECONDS)
//                        .recordStats() // 통계 수집 활성화
//                // TODO: 캐시 항목 제거 리스너 필요?
//        );
//
//        return cacheManager;
//    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
                        )
                )
                .prefixCacheNameWith("discodeit:")
                .entryTtl(Duration.ofSeconds(600))
                .disableCachingNullValues();
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration())
                .build();
    }
}
