package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token); // 토큰 문자열로 조회 -> 갱신 시 검증용

    Optional<RefreshToken> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    void deleteByToken(String token);
}
