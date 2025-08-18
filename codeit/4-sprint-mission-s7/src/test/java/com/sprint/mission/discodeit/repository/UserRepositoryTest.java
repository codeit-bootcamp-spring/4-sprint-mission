package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.JpaAuditingTestConfig;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({JpaAuditingTestConfig.class})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void 유저_저장_성공() {
        //given
        User user = UserFixture.createUserJane();
        //when
        User saved = userRepository.save(user);
        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo(user.getUsername());
        assertThat(saved.getPassword()).isEqualTo(user.getPassword());
        assertThat(saved.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void 유저_저장_실패() { //동일한 이메일
        //given
        User user = UserFixture.createUserJane();
        User user2 = UserFixture.createUserJane_DuplicateEmail();
        userRepository.save(user);
        userRepository.flush();

        //when + then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
            userRepository.flush();
        });
    }
}
