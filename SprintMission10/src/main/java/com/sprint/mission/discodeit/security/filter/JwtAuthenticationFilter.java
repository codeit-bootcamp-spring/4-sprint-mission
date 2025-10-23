package com.sprint.mission.discodeit.security.filter;

import ch.qos.logback.core.util.StringUtil;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthorityUtils authorityUtils;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthorityUtils authorityUtils) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.authorityUtils = authorityUtils;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String authorizationHeader = request.getHeader("Authorization");

    if(!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")){
      filterChain.doFilter(request, response);
      return;
    }

    try{
      Map<String, Object> claims = verifyJws(request);
      setAuthenticationToContext(claims);
    }catch (Exception e) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private Map<String, Object> verifyJws(HttpServletRequest request) {
    String jws = request.getHeader("Authorization").replace("Bearer ", "");
    Map<String, Object> claims = jwtTokenProvider.getClaims(jws);
    return claims;
  }

  private void setAuthenticationToContext(Map<String, Object> claims) {
    String username = (String) claims.get("username");
    List<GrantedAuthority> authorities = authorityUtils.createAuthorityList((List)claims.get("roles"));
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}

