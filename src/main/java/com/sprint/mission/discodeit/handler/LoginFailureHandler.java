package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException {

    // 항상 동일한 메시지와 상태코드로 응답
    int status = HttpServletResponse.SC_UNAUTHORIZED; // 401 고정
    String username = request.getParameter("username");

    // 로그에는 상세 사유를 남기되, 응답에는 숨김
    log.warn("로그인 실패: username={}, ip={}, reason={}",
        username, request.getRemoteAddr(), exception.getMessage());


    DiscodeitException discodeitException = new DiscodeitException(ErrorCode.AUTH_FAILURE);
    discodeitException.addDetail("username", username);
    discodeitException.addDetail("path", request.getRequestURI());
    discodeitException.addDetail("ip", request.getRemoteAddr());

    // ErrorResponse 생성
    // 클라이언트에는 항상 동일한 '로그인 실패' 메시지만 노출
    ErrorResponse errorResponse = new ErrorResponse(
        discodeitException.getTimestamp(),
        discodeitException.getErrorCode().name(),
        "로그인 실패", // 고정 메시지
        discodeitException.getDetails(),
        discodeitException.getClass().getSimpleName(),
        status
    );

    response.setStatus(status);
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}