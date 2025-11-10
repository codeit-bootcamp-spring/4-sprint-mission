package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.jwt.dto.JwtDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    // 인증된 사용자 정보 가져오기
    User user =  (User) authentication.getPrincipal();

    // Access 토큰, RefreshToken 생성
    Date accessTokenExp = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getAccessTokenExpirationMinutes());
    Date refreshTokenExp = jwtTokenProvider.getTokenExpiration(jwtTokenProvider.getRefreshTokenExpirationMinutes());

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", user.getEmail());
    claims.put("roles", user.getRole());

    try {
      String accessToken = jwtTokenProvider.generateAccessToken(
          claims, user.getEmail(), accessTokenExp);

      String refreshToken = jwtTokenProvider.generateRefreshToken(
          user.getEmail(), refreshTokenExp);

      // Refresh Token 쿠키에 저장
      Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
      refreshCookie.setHttpOnly(true);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60);
      response.addCookie(refreshCookie);

      // BinaryContentDto 매핑
      BinaryContentDto profileDto = null;
      if (user.getProfile() != null) {
        profileDto = new BinaryContentDto(
            user.getProfile().getId(),
            user.getProfile().getFileName(),
            user.getProfile().getSize(),
            user.getProfile().getContentType()
        );
      }

      // UserDto 생성
      UserDto userDto = new UserDto(
          user.getId(),          // User 엔티티에서 가져오기
          user.getUsername(),
          user.getEmail(),
          profileDto,
         true,      // Boolean 필드 getter
          user.getRole()
      );

      // Access Token 응답 Body로 반환
      JwtDto jwtDto = new JwtDto(userDto, refreshToken);

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.setStatus(HttpServletResponse.SC_OK);
      objectMapper.writeValue(response.getWriter(), jwtDto);

    } catch (Exception e) {
      throw new RuntimeException("JWT 생성 중 오류 발생", e);
    }
  }


}
