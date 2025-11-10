package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.interceptor.JwtAuthenticationChannelInterceptor;
import com.sprint.mission.discodeit.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;


  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/sub");
    registry.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 클라이언트가 연결할 WebSocket 엔드포인트 정의
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*") // CORS 허용
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(jwtAuthenticationChannelInterceptor,
        new SecurityContextChannelInterceptor(),
        authorizationChannelInterceptor());
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .anyMessage().hasRole(Role.USER.name())
            .build()
    );
  }
}
