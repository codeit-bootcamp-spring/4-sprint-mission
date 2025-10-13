package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initAdminIfAbsent() {
        return args -> {
            // 어드민 계정이 없는 경우에만 초기화
            boolean adminExists = userRepository.existsByRole(Role.ADMIN);
            if (adminExists) {
                log.info("[admin-init] ADMIN already exists. skip");
                return;
            }

            // 예시 기본 값 (필요 시 변경)
            String username = "admin";
            String email = "admin@example.com";
            String rawPassword = "changeMe123!";

            User admin = new User(
                    username,
                    email,
                    passwordEncoder.encode(rawPassword),
                    null, // profile 없음
                    Role.ADMIN
            );
            admin.updateRole(Role.ADMIN);

            userRepository.save(admin);
            log.info("[admin-init] ADMIN initialized: username='{}', email='{}'", username, email);
        };
    }
}
