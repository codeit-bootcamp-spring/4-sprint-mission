package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.jwt.JwtDto;
import com.sprint.mission.discodeit.jwt.JwtProvider;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtProvider jwtProvider;
  //private final RefreshTokenService refreshTokenService;
  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${jwt.refresh-token-expiration-minutes}")
  private long refreshTokenExpirationMinutes;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    // 로그인에 성공한 유저
    DiscodeitUserDetails ud = (DiscodeitUserDetails) authentication.getPrincipal();

    // 토큰 발급
    String accessToken = jwtProvider.createAccessToken(ud);
    String refreshToken = jwtProvider.createRefreshToken(ud.getUsername());
    createRefreshToken(ud.getUserDto().id(), refreshToken, refreshTokenExpirationMinutes);

    // 리프레시 토큰을 쿠키로 발행
    ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(jwtProvider.getRefreshTtlSeconds())
        .sameSite("Lax")
        .build();
    response.addHeader("Set-Cookie", refreshCookie.toString());

    // 응답 바디
    JwtDto responseBody = new JwtDto(ud.getUserDto(), accessToken);

    response.setStatus(HttpStatus.OK.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), responseBody);

    log.debug("로그인 성공: username={}, userId={}", ud.getUserDto().username(), ud.getUserDto().id());
  }

  private void createRefreshToken(UUID userId, String tokenValue ,long expirationMinutes) {
    LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(expirationMinutes);

    RefreshToken refreshToken = RefreshToken.builder()
        .token(tokenValue)
        .userId(userId)
        .expiredAt(expiredAt)
        .rotated(false)
        .build();

    refreshTokenRepository.save(refreshToken);
  }
}