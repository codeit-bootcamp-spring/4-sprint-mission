package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("JWT 토큰 검증 필터 시작");
        try {
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        } catch (Exception e) {
            request.setAttribute("exception", e);
            log.error("JWT 토큰 검증 중 알 수 없는 에러 발생: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        log.debug("Authorization: {}", authorization);
        return authorization == null || !authorization.startsWith("Bearer");
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ", "");
        log.debug("JWS: {}", jws);
        return jwtTokenProvider.getClaims(jws);
    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        log.debug("Spring Context에 Authentication 추가 - claims: {}", claims);
        String username = claims.get("sub").toString(); // sub에 username 담김
        String roleName = "ROLE_" + claims.get("role");
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
