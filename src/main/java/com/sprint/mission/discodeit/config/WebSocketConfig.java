package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // 웹소켓 메시지 브로커 설정
    // 메모리 기반 SimpleBroker 사용
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // Destination Prifix, 클라이언트에서 메시지 구독할때 씀
        registry.setApplicationDestinationPrefixes("/pub"); // Application Destination Prefix, 클라이언트에서 메시지 발행할때 씀
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("ws") // 엔드포인트
                .withSockJS(); // SockJS 연결 지원
    }
}
