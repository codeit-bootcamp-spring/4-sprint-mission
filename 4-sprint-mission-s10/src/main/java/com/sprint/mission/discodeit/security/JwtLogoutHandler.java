package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class JwtLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        ResponseCookie deleteRefresh = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(true)      // 로그인 시 secure 사용했다면 동일
                .path("/")         // 로그인 시 path="/"였으므로 동일
                .sameSite("Strict")// 로그인 시 Strict였다면 동일
                .maxAge(0)         // 즉시 만료
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
    }
}
