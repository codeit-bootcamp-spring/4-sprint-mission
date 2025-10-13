package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        // 예외 → 표준 코드/메시지 매핑
        String code;
        String message;

        if (exception instanceof BadCredentialsException) {
            code = "AUTH_BAD_CREDENTIALS";
            message = "이메일 또는 비밀번호가 올바르지 않습니다.";
        } else if (exception instanceof UsernameNotFoundException) {
            code = "AUTH_USER_NOT_FOUND";
            message = "존재하지 않는 사용자입니다.";
        } else if (exception instanceof DisabledException) {
            code = "AUTH_USER_DISABLED";
            message = "비활성화된 계정입니다.";
        } else if (exception instanceof LockedException) {
            code = "AUTH_USER_LOCKED";
            message = "잠긴 계정입니다.";
        } else if (exception instanceof AccountExpiredException) {
            code = "AUTH_ACCOUNT_EXPIRED";
            message = "만료된 계정입니다.";
        } else if (exception instanceof CredentialsExpiredException) {
            code = "AUTH_CREDENTIALS_EXPIRED";
            message = "비밀번호가 만료되었습니다.";
        } else {
            code = "AUTH_FAILURE";
            message = "인증에 실패했습니다.";
        }

        Map<String, Object> details = new HashMap<>();
        details.put("path", request.getRequestURI());

        // ErrorResponse 구성
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                code,
                message,
                details,
                exception.getClass().getSimpleName(),
                HttpServletResponse.SC_UNAUTHORIZED
        );

        // 401 + JSON
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
