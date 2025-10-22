package com.sprint.mission.discodeit.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      // Authorization 헤더 추출
      String authHeader = request.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      // 토큰 추출
      String token = authHeader.substring(7);

      // 토큰 유효성 검사
      if (!jwtTokenProvider.validateToken(token)) {
        log.debug("Invalid JWT token");
        filterChain.doFilter(request, response);
        return;
      }

      // 토큰에서 이메일(또는 username) 추출
      String email = jwtTokenProvider.getEmailFromToken(token);

      // 5. SecurityContext에 인증 정보 설정
      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        userRepository.findByEmail(email).ifPresent(user -> {
          // 🔹 엔티티 → DTO 변환
          UserDto userDto = userMapper.toDto(user);

          // 🔹 UserDetails 구현체 생성
          DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, user.getPassword());

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities()
              );

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);

          log.debug("JWT 인증 완료: {}", email);
        });
      }
    } catch (Exception e){

      log.error("JWT 인증 필터 처리 중 오류", e);
    }

    // 다음 필터로 진행
    filterChain.doFilter(request, response);
  }

}

