package com.sprint.mission.discodeit.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyLoggedInException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.exception.user.UserNotActiveException;
import io.micrometer.core.instrument.config.validate.Validated.Invalid;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    UserException userException;
    int status;

    if (exception instanceof DisabledException) {
      userException = new UserNotActiveException();
      status = HttpStatus.UNAUTHORIZED.value();
    } else if (exception instanceof SessionAuthenticationException) {
      userException = new UserAlreadyLoggedInException();
      status = HttpStatus.CONFLICT.value();
    } else {
      userException = new InvalidCredentialsException();
      status = HttpStatus.UNAUTHORIZED.value();
    }

    ErrorResponse errorResponse = new ErrorResponse(userException, status);
    response.setStatus(status);
    response.setContentType("application/json;charset=UTF-8");
    String json = objectMapper.writeValueAsString(errorResponse);
    response.getWriter().write(json);
  }

}
