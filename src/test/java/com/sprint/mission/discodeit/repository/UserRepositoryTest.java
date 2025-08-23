package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @DisplayName("UserStatus 없는 User는 조회 X")
    void findAllWithProfileAndStatusWhenUserHasNoStatusThenReturnEmptyList() {
        // given
        User user = new User(
                "usename",
                "email@codeit.com",
                "password",
                null
        );
        userRepository.save(user);

        // when
        List<User> result = userRepository.findAllWithProfileAndStatus();

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("UserStatus 있는 User는 조회 O")
    void findAllWithProfileAndStatusWhenUserHasStatusThenReturnUserList() {
        // given
        User user = new User(
                "usename",
                "email@codeit.com",
                "password",
                null
        );
        UserStatus userStatus = new UserStatus(user, Instant.now());
        userRepository.save(user);
        userStatusRepository.save(userStatus);

        // when
        List<User> result = userRepository.findAllWithProfileAndStatus();

        // then
        assertEquals(1, result.size());
    }
}