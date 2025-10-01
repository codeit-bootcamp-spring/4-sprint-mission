package com.sprint.mission.discodeit.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    public LoginFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("접근 권한이 없음 msg = {}", exception.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Cookie rememberCookie = new Cookie("remember-me", null);
        rememberCookie.setMaxAge(0); // 즉시 만료
        rememberCookie.setPath("/");
        Cookie sessionIdCookie = new Cookie("JSESSIONID", null);
        sessionIdCookie.setMaxAge(0); // 즉시 만료
        sessionIdCookie.setPath("/");
        response.addCookie(rememberCookie);
        response.addCookie(sessionIdCookie);
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(exception));
    }
}
