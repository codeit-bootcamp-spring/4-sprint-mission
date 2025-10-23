package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public RefreshToken saveRefreshToken(UUID userId) {
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(userId.toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue) // TODO: 리프래시 토큰 암호화 필요
                .user(user)
                .expiredAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60 * 1000))
                .rotated(false)
                .build();

        log.info("# Refresh token 저장 완료, refresh token: {}", refreshToken);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByUserId(UUID userId) {
        return refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(userId + "의 Refresh Token이 존재 X"));
    }

    public void invalidateToken(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("# Refresh Token 폐기 처리 완료");
    }

    public RefreshToken rotatedToken(UUID userId) {
        RefreshToken oldToken = findByUserId(userId);
        oldToken.invalidate();
        refreshTokenRepository.save(oldToken);

        String newTokenValue = jwtTokenProvider.generateRefreshToken(userId.toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        RefreshToken refreshToken = RefreshToken.builder()
                .token(newTokenValue)
                .user(user)
                .expiredAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60 * 1000))
                .rotated(true)
                .build();
        log.info("# Refresh Token Rotate 성공, new refresh token: {}", refreshToken);

        return refreshTokenRepository.save(refreshToken);
    }
}
