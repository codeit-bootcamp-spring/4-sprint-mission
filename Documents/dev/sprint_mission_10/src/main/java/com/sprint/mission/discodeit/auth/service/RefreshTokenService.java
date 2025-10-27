package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.auth.entity.RefreshToken;
import com.sprint.mission.discodeit.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private static final long DEFAULT_EXPIRATION_MINUTES = 60 * 24 * 7;
    //Refresh Token 생성 및 저장
    public RefreshToken createRefreshToken(RefreshToken token) {
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expirationAt = LocalDateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .userId(token.getUserId())
                .expiredAt(expirationAt)
                .rotated(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    // 특정 사용자 토큰 조회
    public RefreshToken findByUserId(UUID userId) {
        return refreshTokenRepository.findByUserId(userId)
                .orElseThrow(()-> new IllegalArgumentException("해당 사용자의 Refresh Token이 존재하지 않습니다."));
    }

    //토큰 폐기 처리 (로그아웃 등)
    public void invalidateToken(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    //토큰 회전
    public RefreshToken rotateRefreshToken(RefreshToken refreshToken) {
        RefreshToken oldToken = findByUserId(refreshToken.getUserId());
        oldToken.invalidate();

        String newTokenValue = UUID.randomUUID().toString();
        RefreshToken newToken = RefreshToken.builder()
                .token(newTokenValue)
                .userId(refreshToken.getUserId())
                .expiredAt(LocalDateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES))
                .rotated(true)
                .build();

        refreshTokenRepository.save(newToken);
        return newToken;
    }
}
