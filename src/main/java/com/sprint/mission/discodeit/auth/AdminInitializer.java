package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void initializeAdmin() {

    if (!userRepository.existsByRole(Role.ADMIN)) {
      User admin = new User(
          "admin",
          "admin@discodeit.com",
          passwordEncoder.encode("1234"),
          null,
          Role.ADMIN
      );
      userRepository.save(admin);
    }
  }
}