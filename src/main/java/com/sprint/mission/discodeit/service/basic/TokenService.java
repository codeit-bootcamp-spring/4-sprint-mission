package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.response.TokenResponse;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**
   * Refresh Token 검증하고 Access Token 재발급
   */
  @Transactional
  public TokenResponse reissueToken(String refreshTokenValue) {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token"));

    if (refreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("만료된 Refresh Token - 다시 로그인해주세요");
    }

    User user = userRepository.findById(refreshToken.getUserId())
        .orElseThrow(UserNotFoundException::new);
    UUID userId = user.getId();

    // 새로운 access Token
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("email", user.getEmail());
    claims.put("username", user.getUsername());
    claims.put("role", user.getRole().name());
    String subject = user.getId().toString();
    String newAccessToken = jwtTokenProvider.generateAccessToken(claims, subject);

    String newRefreshTokenValue = refreshToken.getToken(); // old token

    // rotating by update
    if (shouldRotate(refreshToken)) {
      long expirationMinutes = jwtTokenProvider.getRefreshTokenExpirationMinutes();
      LocalDateTime newExpiration = LocalDateTime.now().plusMinutes(expirationMinutes);
      newRefreshTokenValue = jwtTokenProvider.generateRefreshToken(userId.toString());

      refreshToken.setToken(newRefreshTokenValue);
      refreshToken.setExpiredAt(newExpiration);
//      refreshTokenRepository.save(refreshToken);
    }
    return new TokenResponse(userMapper.toDto(user), newAccessToken, newRefreshTokenValue);
  }

  /**
   * Refresh Token 회전 여부 판단 로직 일정 주기(예: 만료 3일 전 등)에 따라 새 토큰 발급
   */
  private boolean shouldRotate(RefreshToken token) {

    return token.getExpiredAt().isBefore(LocalDateTime.now().plusDays(3));
  }
}
