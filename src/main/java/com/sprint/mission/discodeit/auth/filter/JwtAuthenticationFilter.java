package com.sprint.mission.discodeit.auth.filter;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;


// 클라이언트 측에서 전송된 request header에 포함된 JWT에 대해 검증 작업을 수행
// verifyng the token that the user already has
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // claims 가져오기 (유효성 검사 포함)
    Map<String, Object> claims = verifyJws(request);

    // userDetail 가져오기
    String username = claims.get("username").toString();
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // continue to next chain
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String authorization = request.getHeader("Authorization");
    return authorization == null || !authorization.startsWith("Bearer");
  }

  // ======================== private methods ========================
  private Map<String, Object> verifyJws(HttpServletRequest request) {
    String jws = request.getHeader("Authorization").replace("Bearer ", "");
    return jwtTokenProvider.getClaims(jws); // 유효성 검사 포함
  }
}