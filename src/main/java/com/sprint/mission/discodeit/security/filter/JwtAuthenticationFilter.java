package com.sprint.mission.discodeit.security.filter;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/*
 * JWT 인증 필터
 *
 * - JWT 검증은 request 당 한 번만 수행하면 되기 때문에 OncePerRequestFilter를 이용하는 것이 적절
 */

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없으면 바로 다음 필터로
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessTokenValue = authorizationHeader.substring(7); // "Bearer " 제거

        try {
            if (jwtTokenProvider.validateToken(accessTokenValue)) {
                Map<String, Object> claims = jwtTokenProvider.getClaims(accessTokenValue);
                UUID userId = UUID.fromString(claims.get("sub").toString());

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> UserNotFoundException.withId(userId));

                DiscodeitUserDetails userDetails =
                        new DiscodeitUserDetails(userMapper.toDto(user), user.getPassword());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("# JwtAuthenticationFilter - Access Token 인증 성공: {}", user.getEmail());
            } else {
                if (jwtTokenProvider.isTokenExpired(accessTokenValue)) {
                    log.debug("# JwtAuthenticationFilter - Access Token 만료");
                    throw new AuthenticationServiceException("Access Token 만료");
                } else {
                    log.debug("# JwtAuthenticationFilter - Access Token 검증 실패");
                    throw new AuthenticationServiceException("Access Token 검증 실패");
                }
            }
        } catch (Exception e) {
            log.error("# JwtAuthenticationFilter 예외 발생: {}", e.getMessage());
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/"); // 인증 관련 경로는 필터 제외
    }
    /*private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.debug("# JwtAuthenticationFilter.doFilterInternal()");
        String accessTokenValue = request.getHeader("Authorization").replace("Bearer ", "");

        if (jwtTokenProvider.validateToken(accessTokenValue)) {
            Map<String, Object> claims = jwtTokenProvider.getClaims(accessTokenValue);

            UUID userId = UUID.fromString(claims.get("sub").toString());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> UserNotFoundException.withId(userId));
            DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userMapper.toDto(user), user.getPassword());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("# JwtAuthenticationFilter 성공");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.debug("# JwtAuthenticationFilter.shouldNotFilter()");
        String authorization = request.getHeader("Authorization");

        return authorization == null || !authorization.startsWith("Bearer");
    }*/
}
