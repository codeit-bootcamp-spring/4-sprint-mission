package com.sprint.mission.discodeit.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * AuthenticationSuccessHandler 컴포넌트 대체
 *
 * AuthenticationSuccessHandler: 사용자의 인증이 성공했을 때 호출되는 인터페이스
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionRegistry sessionRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal(); // getPrincipal(): 주체, 사용자 ID(username)이나 UserDetails 객체 등 사용자 신원 식별 정보
        UserDto userDto = userDetails.getUserDto();
        log.debug("Login successful for user: {}", userDto.username());

        String sessionId = request.getSession().getId();
        sessionRegistry.registerNewSession(sessionId, authentication.getPrincipal());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK); // 200 상태코드 반환
        response.getWriter().write(new ObjectMapper().writeValueAsString(userDto));
    }
}
