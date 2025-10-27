package com.sprint.mission.discodeit.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");
        return authorization == null || !authorization.startsWith("Bearer ");
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("JWT 토큰이 존재하지 않습니다.");
        }
        String jws = authorization.replace("Bearer ","");
        boolean isValid = jwtTokenProvider.validateToken(jws);
        if (!isValid) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        }
        return jwtTokenProvider.getClaims(jws);
    }

    private void setAuthenticationToContext(Map<String, Object> claims) {
        String username = (String) claims.get("sub"); // JWT의 subject
        if (username == null) return;

        // 2. 권한 정보 추출
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null) {
            for (String role : roles) {
                authorities.add(() -> role); // 람다로 SimpleGrantedAuthority 대체 가능
            }
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

