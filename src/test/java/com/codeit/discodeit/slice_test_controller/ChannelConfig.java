package com.codeit.discodeit.slice_test_controller;

import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.service.UserStatusService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ChannelConfig {

  @Bean
  public ChannelService channelService() {
    return Mockito.mock(ChannelService.class); // 인터페이스 타입 Mock
  }
}