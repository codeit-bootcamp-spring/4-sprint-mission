package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class JwtLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("Logout 시작");
        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new RuntimeException("cookies is null");

        Optional<Cookie> optionalRefreshCookie = Arrays.stream(cookies)
                .filter(c -> "REFRESH_TOKEN".equals(c.getName()))
                .findFirst();
        if (optionalRefreshCookie.isEmpty()) throw new RuntimeException("refresh cookie is empty");
        Cookie refreshCookie = optionalRefreshCookie.get();

        Cookie deleteCookie = new Cookie("REFRESH_TOKEN", refreshCookie.getValue());
        deleteCookie.setMaxAge(0);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);
    }
}
