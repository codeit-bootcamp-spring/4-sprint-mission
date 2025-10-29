package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.debug("인증 성공, 토큰 생성 시작");
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            response.setStatus(HttpServletResponse.SC_OK);
            UserDto userDto = userDetails.getUserDto();

            // 클레임 생성
            Map<String, Object> claims = new HashMap<>();
                claims.put("role", userDto.role().name());

            // 토큰 발급
            String accessToken = jwtTokenProvider.generateAccessToken(claims, userDto.username());
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDto.username());
            log.debug("accessToken: {}", accessToken);
            log.debug("refreshToken: {}", refreshToken);

            log.debug("Authorization Header 추가");
            response.setHeader("Authorization", "Bearer " + accessToken);

            // 리프레시 토큰 쿠키 저장
            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60);
            response.addCookie(refreshCookie);

            // 응답
            JwtDto jwtDto = new JwtDto(userDto, accessToken);
            response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse(
                    new RuntimeException("Authentication failed: Invalid user details"),
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
