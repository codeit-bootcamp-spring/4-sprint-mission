package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final RefreshTokenRepository refreshTokenRepository;


  @Override
  @Transactional // 로그아웃 안돼서 추가
  // refreshTokenRepository.deleteByToken이 트랜색션 밖에서 실행됐는데, DB작업은 무조건 트랜색션에 감싸져야 한다
  // 로그아웃 핸들러는 자동으로 트랜색션을 만들어주지 않는다
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return;
    }
    Optional<Cookie> cookieOptional = Arrays.stream(cookies)
        .filter(c -> c.getName().equals("REFRESH_TOKEN"))
        .findFirst();
    if (cookieOptional.isPresent()) {
      String refreshToken = cookieOptional.get().getValue();
      refreshTokenRepository.deleteByToken(refreshToken);

      // 브라우저에서 쿠키 제거
      Cookie clearedCookie = new Cookie("REFRESH_TOKEN", null);
      clearedCookie.setPath("/");
      clearedCookie.setMaxAge(0); // 즉시 제거
      response.addCookie(clearedCookie);
    }
  }
}
