package com.sprint.mission.discodeit.auth.registry;

import com.sprint.mission.discodeit.auth.provider.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {
    private final JwtTokenProvider jwtTokenProvider;

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;

    public InMemoryJwtRegistry(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        log.info("JwtInformation 등록 user = {}", jwtInformation.getUserDto().username());
        if (!origin.containsKey(jwtInformation.getUserDto().id())) {
            origin.put(jwtInformation.getUserDto().id(), new ConcurrentLinkedQueue<>());
        }
        Queue<JwtInformation> queue = origin.get(jwtInformation.getUserDto().id());
        if (queue.size() >= maxActiveJwtCount) {
            queue.poll();
        }
        queue.offer(jwtInformation);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        log.debug("JwtInformation 삭제 userId = {}", userId);
        origin.remove(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        log.debug("JwtInformation 등록 유저 검증 userId = {}", userId);
        Queue<JwtInformation> queue = origin.get(userId);
        if (queue == null || queue.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        log.debug("JwtInformation AccessToken 검증 accessToken = {}", accessToken);
        return origin.values()
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(jwtInformation -> jwtInformation.getAccessToken().equals(accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        log.debug("JwtInformation Refresh Token 검증 refreshToken = {}", refreshToken);
        return origin.values()
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        log.info("JwtInformation RefreshToken Rotate user = {}", newJwtInformation.getUserDto().username());
        UUID userId = origin.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(jwtInfo ->
                        jwtInfo.getRefreshToken().equals(refreshToken))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("JwtInformation 내 찾을 수 없음 refreshToken = {}", refreshToken);
                    throw new IllegalArgumentException("Invalid refresh token");
                })
                .getUserDto().id();
        Queue<JwtInformation> queue = origin.get(userId);
        if (queue.size() >= maxActiveJwtCount) {
            queue.poll();
        }
        queue.offer(newJwtInformation);
    }

    @Scheduled(fixedRate = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        log.info("유효기간이 만료된 JwtInformation 정리");
        origin.forEach((userId, queue) -> {
            queue.removeIf(jwtInformation ->
                    jwtTokenProvider.isExpired(jwtInformation.getRefreshToken()));
            if (queue.isEmpty()) {
                origin.remove(userId);
            }
        });
    }
}
