package com.codeit.discodeit8.slice_test_controller;

import com.codeit.discodeit8.service.UserService;
import com.codeit.discodeit8.service.UserStatusService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UserConfig {

  @Bean
  public UserService userService() {
    return Mockito.mock(UserService.class); // 인터페이스 타입 Mock
  }

  @Bean
  public UserStatusService userStatusService() {
    return Mockito.mock(UserStatusService.class); // 인터페이스 타입 Mock
  }
}
