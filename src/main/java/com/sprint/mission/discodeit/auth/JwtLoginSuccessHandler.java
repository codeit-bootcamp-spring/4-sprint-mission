package com.sprint.mission.discodeit.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;

  // request = original incoming login request
  // response = the canvas we use to build the response that goes back!
  // authentication = the data
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    /**
     * 인증 성공 시 JwtProvider를 활용해 토큰을 발급하세요.
     * 리프레시 토큰은 쿠키(REFRESH_TOKEN)에 저장하세요.
     * 200 JwtDto로 응답합니다.
     */

    // extract the principal from the authentication object
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    UserDto userDto = userDetails.getUserDto();

    String subject = userDto.userId().toString();
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", userDto.username());
    claims.put("role", userDto.role());
    claims.put("email", userDto.email());

    String accessToken = jwtTokenProvider.generateAccessToken(claims, subject);
    String refreshToken = jwtTokenProvider.generateRefreshToken(subject);

    // general response settings
    response.setStatus(HttpStatus.OK.value());
    response.setContentType("application/json;charset=UTF-8");

    // refresh token 은 쿠키로
    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    int refreshTokenExpirationSeconds = jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60;
    refreshTokenCookie.setMaxAge(refreshTokenExpirationSeconds);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);

    // access token은 응답 JwtDto로 Body에 포함
    JwtDto jwtDto = new JwtDto(userDto, accessToken);
    response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
  }
}
