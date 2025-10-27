package com.sprint.mission.discodeit.auth.repository;

import com.sprint.mission.discodeit.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    //특정 사용자의 토큰 조회
    Optional<RefreshToken> findByUserId(UUID userId);
    //토큰 문자열로 조회(갱신 시 검증용)
    Optional<RefreshToken> findByToken(String token);
    //로그아웃 시 해당 사용자의 토큰 제거
    void deleteByUserId(UUID userId);
}
