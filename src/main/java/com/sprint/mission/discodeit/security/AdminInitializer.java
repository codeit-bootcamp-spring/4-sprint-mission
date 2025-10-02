package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient.Builder;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    //어드민 계정이 존재하는지 확인
    log.info("어드민 계정 초기화 시작");
    if(!userRepository.existsByUsername("admin")){
      User admin = User.builder()
          .username("admin")
          .email("admin@admin.com")
          .password(passwordEncoder.encode("admin123!!"))
          .role(Role.ADMIN)
          .build();

      UserStatus userStatus = new UserStatus(admin, Instant.now());

      userRepository.save(admin);
    log.info("어드민 계정 초기화 완료");
    }

  }

}
