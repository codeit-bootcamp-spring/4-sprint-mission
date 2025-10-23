package com.sprint.mission.discodeit.auth.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.entity.RefreshToken;
import com.sprint.mission.discodeit.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.*;

// 로그인 인증을 담당하는 Security Filter(JwtAuthenticationFilter)가 클라이언트의 로그인 인증 정보 수신
@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

  // 로그인 인증 정보(Username/Password)를 전달받아 UserDetailsService와 인터랙션 한 뒤 인증 여부를 판단
  private final AuthenticationManager authenticationManager;

  // 클라이언트가 인증에 성공할 경우, JWT를 생성 및 발급
  private final JwtTokenProvider jwtTokenProvider;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final RefreshTokenRepository refreshTokenRepository;

  // 1) 받은 인증정보를 AuthenticationManager에게 전달해 인증처리 위임
  @SneakyThrows
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) {

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    username = (username != null) ? username : "";
    password = (password != null) ? password : "";

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        username,
        password
    );

    // DiscodeitUserDetailsService에게 사용자의 UserDetails 조회를 위임하고 사용자의 크레덴셜을 조회하고,
    // AuthenticationManager에게 사용자의 UserDetails를 전달 (loadUserByUsername())
    return authenticationManager.authenticate(authenticationToken);
  }

  // 2) 인증이 성공될때 호출됨
  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult) throws IOException {

    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authResult.getPrincipal();
    UserDto user = userDetails.getUserDto();
    String accessToken = delegateAccessToken(user);
    String refreshToken = delegateRefreshToken(user);

    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);

    Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserId(
        user.userId());

    RefreshToken refreshTokenToSave;
    if (existingRefreshToken.isPresent()) {
      RefreshToken foundToken = existingRefreshToken.get();
      foundToken.updateToken(refreshToken, jwtTokenProvider.getRefreshTokenExpirationMinutes());
      refreshTokenToSave = foundToken;
      System.out.println("Updating existing refresh token for user: " + user.userId());
    } else {
      LocalDateTime expirationDate = LocalDateTime.now().plusMinutes(
          jwtTokenProvider.getRefreshTokenExpirationMinutes());
      refreshTokenToSave = RefreshToken.builder()
          .userId(user.userId())
          .token(refreshToken)
          .expiredAt(expirationDate)
          .rotated(false)
          .build();
      System.out.println("Creating new refresh token for user: " + user.userId());
    }

    refreshTokenRepository.save(refreshTokenToSave);

    // setting response body
    JwtDto jwtDto = new JwtDto(user, accessToken);
    String responseBody = objectMapper.writeValueAsString(jwtDto);
    response.getWriter().write(responseBody);

  }

  // ====================== private methods ======================
  // access token 생성
  private String delegateAccessToken(UserDto user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", user.username());
    claims.put("email", user.email());
    claims.put("role", user.role());
    String subject = user.userId().toString();

    return jwtTokenProvider.generateAccessToken(claims, subject);
  }

  // refresh token 생성
  private String delegateRefreshToken(UserDto user) {
    String subject = user.userId().toString();

    return jwtTokenProvider.generateRefreshToken(subject);
  }
}
