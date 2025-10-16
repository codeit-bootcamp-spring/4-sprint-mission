package com.sprint.mission.discodeit.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {  // (1)

  private final JwtProvider jwtProvider;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    log.info("Jwt 인증 필터 시작");
    
    String auth = request.getHeader("Authorization"); // 요청 헤덩 Authorization이 있어야 함
    if (auth == null || !auth.startsWith("Bearer ")) { // Authorization의 값이 Bearer 이어야 함
      filterChain.doFilter(request, response);
      log.info("유효하지 않은 인증 헤더 입니다. Authorization: {}",auth);
      return;
    }
    String accessToken = auth.substring("Bearer ".length());

    try {
      if (!jwtProvider.validateAccessToken(accessToken)) {
        filterChain.doFilter(request, response);
        return;
      }

      String userId = jwtProvider.getSubject(accessToken);
      // userId가 들어온다고
      // TODO
      if (!userId.isEmpty()) {
        UserDetails userDetails = discodeitUserDetailsService.loadUserByUserId(
            UUID.fromString(userId));

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception ex) {
      // 토큰 파싱/검증 중 오류 → 인증 없이 계속 진행(필요 시 로깅/메트릭)
      log.debug("JWT 인증 처리 중 예외: {}", ex.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
