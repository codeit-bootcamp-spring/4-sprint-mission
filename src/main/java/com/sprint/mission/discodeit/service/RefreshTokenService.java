package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.jwt.RefreshTokenEmptyException;
import com.sprint.mission.discodeit.exception.jwt.RefreshTokenExpiredException;
import com.sprint.mission.discodeit.exception.jwt.RefreshTokenNotFoundException;
import com.sprint.mission.discodeit.exception.jwt.RefreshTokenRotatedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.jwt.JwtDto;
import com.sprint.mission.discodeit.jwt.JwtProvider;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  @Value("${jwt.refresh-token-expiration-minutes}")
  private long refreshTokenExpirationMinutes;

  private final JwtProvider jwtProvider;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;
  private final UserRepository userRepository;

  public void createRefreshToken(UUID userId, String tokenValue ,long expirationMinutes) {
    LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(expirationMinutes);

    RefreshToken refreshToken = RefreshToken.builder()
        .token(tokenValue)
        .userId(userId)
        .expiredAt(expiredAt)
        .rotated(false)
        .build();

    refreshTokenRepository.save(refreshToken);
  }

  public RefreshToken findByUserId(UUID userId) {
    return refreshTokenRepository.findByUserId(userId)
        .orElseThrow(() -> RefreshTokenNotFoundException.withId(userId));
  }

  public RefreshToken findByRefreshToken(String refreshTokenValue) {
    return refreshTokenRepository.findByToken(refreshTokenValue)
        .orElseThrow(() -> RefreshTokenNotFoundException.withToken(refreshTokenValue));
  }

  public JwtDto validateRefreshTokenAndReissueAccessToken(String oldTokenValue, HttpServletResponse response) {
    if (oldTokenValue == null || oldTokenValue.isBlank()) {
        throw new RefreshTokenEmptyException();
    }

    RefreshToken oldRefreshToken = findByRefreshToken(oldTokenValue);

    if (oldRefreshToken.isRotated()) {
      // 이미 회전된 토큰의 재사용, 모든 사용자 로그아웃 까지 구현하기
      throw new RefreshTokenRotatedException();
    }

    if (oldRefreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new RefreshTokenExpiredException();
    }

    oldRefreshToken.setRotated(true); // true로 변경하고 저장
    refreshTokenRepository.save(oldRefreshToken);

    String newTokenValue = jwtProvider.createRefreshToken(oldRefreshToken.getId().toString());
    createRefreshToken(oldRefreshToken.getUserId(), newTokenValue, refreshTokenExpirationMinutes);

    UUID userId = oldRefreshToken.getUserId();
    User user = userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.withId(userId));

    DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) discodeitUserDetailsService.loadUserByUsername(user.getUsername());
    String accessToken = jwtProvider.createAccessToken(discodeitUserDetails);

    ResponseCookie newRefreshCookie = ResponseCookie.from("REFRESH_TOKEN", newTokenValue)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(jwtProvider.getRefreshTtlSeconds())
        .sameSite("Lax")
        .build();

    response.addHeader("Set-Cookie", newRefreshCookie.toString()); // 리프레쉬 쿠키 재설정

    return new JwtDto(discodeitUserDetails.getUserDto(), accessToken);
  }

}