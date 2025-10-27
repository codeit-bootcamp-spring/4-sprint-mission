package com.sprint.mission.discodeit.security.jwt;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();

    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();

    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

    private final int maxActiveJwtCount;

    private final JwtProvider tokenProvider;

    public InMemoryJwtRegistry(@Value("${jwt.max-active-count:1}") int maxActiveJwtCount, JwtProvider provider) {
        this.maxActiveJwtCount = maxActiveJwtCount;
        this.tokenProvider = provider;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        origin.compute(jwtInformation.getUserResponse().getId(), (key, queue) -> {
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }
            if (queue.size() >= maxActiveJwtCount) {
                JwtInformation deprecatedJwtInformation = queue.poll();
                if (deprecatedJwtInformation != null) {
                    removeTokenIndex(
                            deprecatedJwtInformation.getAccessToken(),
                            deprecatedJwtInformation.getRefreshToken()
                    );
                    log.debug("최대 토큰 개수 초과로 오래된 토큰 제거: userId={}", key);
                }
            }
            queue.add(jwtInformation);
            addTokenIndex(
                    jwtInformation.getAccessToken(),
                    jwtInformation.getRefreshToken()
            );
            log.debug("JWT 등록 완료: userId={}, 현재 토큰 수={}", key, queue.size());
            return queue;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> removed = origin.remove(userId);
        if (removed != null) {
            removed.forEach(info -> removeTokenIndex(info.getAccessToken(), info.getRefreshToken()));
            log.debug("사용자의 모든 JWT 무효화: userId={}, 제거된 토큰 수={}", userId, removed.size());
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.get(userId);
        return queue != null && !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return accessTokenIndexes.contains(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshTokenIndexes.contains(refreshToken);
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.computeIfPresent(newJwtInformation.getUserResponse().getId(), (key, queue) -> {
            // 기존 방식의 문제: rotate() 반환값을 사용하지 않음
            // 해결: 큐에서 제거 후 새로 추가

            boolean rotated = false;
            Queue<JwtInformation> newQueue = new ConcurrentLinkedQueue<>();

            for (JwtInformation jwtInfo : queue) {
                if (jwtInfo.getRefreshToken().equals(refreshToken)) {
                    // 기존 토큰 인덱스 제거
                    removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());

                    // 새 토큰 추가
                    newQueue.add(newJwtInformation);
                    addTokenIndex(
                            newJwtInformation.getAccessToken(),
                            newJwtInformation.getRefreshToken()
                    );
                    rotated = true;
                    log.debug("JWT 로테이션 완료: userId={}", key);
                } else {
                    newQueue.add(jwtInfo);
                }
            }

            if (!rotated) {
                log.warn("로테이션 대상 RefreshToken을 찾을 수 없음: userId={}", key);
            }

            return newQueue;
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        AtomicInteger totalCleaned = new AtomicInteger();

        origin.entrySet().removeIf(entry -> {
            Queue<JwtInformation> queue = entry.getValue();
            int beforeSize = queue.size();

            queue.removeIf(jwtInformation -> {
                boolean isExpired =
                        !tokenProvider.validateToken(jwtInformation.getAccessToken()) ||
                                !tokenProvider.validateToken(jwtInformation.getRefreshToken());
                if (isExpired) {
                    removeTokenIndex(
                            jwtInformation.getAccessToken(),
                            jwtInformation.getRefreshToken()
                    );
                }
                return isExpired;
            });

            int cleaned = beforeSize - queue.size();
            totalCleaned.addAndGet(cleaned);

            return queue.isEmpty();
        });

        if (totalCleaned.get() > 0) {
            log.info("만료된 JWT 정리 완료: {}개 제거", totalCleaned);
        }
    }

    private void addTokenIndex(String accessToken, String refreshToken) {
        accessTokenIndexes.add(accessToken);
        refreshTokenIndexes.add(refreshToken);
    }

    private void removeTokenIndex(String accessToken, String refreshToken) {
        accessTokenIndexes.remove(accessToken);
        refreshTokenIndexes.remove(refreshToken);
    }
}
