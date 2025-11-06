package com.sprint.mission.discodeit.interceptor;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

//검증 로직은 이전에 구현한 JwtAuthenticationFilter를 참고하세요.
//인증이 완료되면 SecurityContext에 인증정보를 저장하는 대신 accessor 객체에 저장하세요.

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final UserDetailsService userDetailsService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      String authHeader = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
      String jwt;
      if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
        jwt = authHeader.substring(BEARER_PREFIX.length());
      } else {
        throw new RuntimeException("Authorization 헤더가 없거나 Bearer 타입이 아닙니다.") {
        };
      }
      if (jwtTokenProvider.validateAccessToken(jwt)) {
        // 토큰에서 Authentication 객체 생성 (JwtAuthenticationFilter의 로직 재사용)
        String username = jwtTokenProvider.getUsernameFromToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

        // accessor에 인증 정보 저장
        // SecurityContextChannelInterceptor에 의해 SecurityContext에 설정됨
        accessor.setUser(authentication);
      }
    }
    return message;
  }
}
