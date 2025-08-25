package com.codeit.discodeit.slice_test_controller;

import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MessageConfig {

  @Bean
  public MessageService messageService() {
    return Mockito.mock(MessageService.class); // 인터페이스 타입 Mock
  }
}