package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.exception.jwt.RefreshTokenNotFoundException;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import com.sprint.mission.discodeit.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public void logout(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) {
    log.info("로그아웃 처리 시작");

    // 쿠키에서 Refresh Token 꺼내오기
    String refreshToken = null;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("REFRESH_TOKEN".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          break;
        }
      }
    }

    // Refresh Token 무효화
    if (refreshToken != null) {
      invalidateRefreshToken(refreshToken);
      log.debug("리프레시 토큰 무효화 완료: {}", refreshToken);
    }

    // 쿠키 삭제 (클라이언트 브라우저에서 제거)
    Cookie deleteCookie = new Cookie("REFRESH_TOKEN", null);
    deleteCookie.setHttpOnly(true);
    deleteCookie.setSecure(true);
    deleteCookie.setPath("/");
    deleteCookie.setMaxAge(0);
    response.addCookie(deleteCookie);

    SecurityContextHolder.clearContext();

    log.info("로그아웃 완료: user={}",
        authentication != null ? authentication.getName() : "anonymous");
  }

  private void invalidateRefreshToken(String refreshTokenValue) {
    RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
        .orElseThrow(() -> RefreshTokenNotFoundException.withToken(refreshTokenValue));
    refreshToken.setRotated(false);
    refreshTokenRepository.save(refreshToken);
  }
}