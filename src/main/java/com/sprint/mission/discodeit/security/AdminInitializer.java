package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AuthService authService;

    @Override
    public void run(String... args) {
        authService.registerAdmin();
    }
}
