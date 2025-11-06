package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.interceptor.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 사용 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // 서버가 클라이언트로 메시지를 보낼 때 사용하는 prefix (subscribe)
    config.enableSimpleBroker("/sub");
    // 클라이언트가 서버로 메시지를 보낼 때 사용하는 prefix (publish)
    config.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // client가 최초로 연결할 websocket endpoint
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*") // CORS 허용
        .withSockJS(); // SockJS fallback
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    if (jwtAuthenticationChannelInterceptor == null) {
      System.out.println("jwtAuthenticationChannelInterceptor IS NULL");
    }

    registration.interceptors(
        // 인증 - authentication 객체 생성 + accessor에 저장
        jwtAuthenticationChannelInterceptor,
        // Context Propagation - authentication 객체를 securityContextHolder에 저장
        new SecurityContextChannelInterceptor(),
        // 인가 -
        authorizationChannelInterceptor()
    );
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .anyMessage().hasRole(Role.USER.name())
            .build()
    );
  }

}
