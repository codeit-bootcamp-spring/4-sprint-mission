package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        boolean exists = userRepository.existsByRole(Role.ADMIN);
        if (!exists) {
            User admin = new User(
                    "admin",
                    "admin@gmail.com"
                    , passwordEncoder.encode("admin1!"),
                    null,
                    Role.ADMIN
            );

            userRepository.save(admin);
            log.debug("Created Admin obj");
        } else {
            log.debug("Admin already exists");
        }

    }
}
