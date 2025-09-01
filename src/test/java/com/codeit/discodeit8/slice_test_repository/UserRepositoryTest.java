package com.codeit.discodeit8.slice_test_repository;
import com.codeit.discodeit8.auditing_config.AuditingConfig;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // yaml 파일을 test로
@Import(AuditingConfig.class)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void 이메일로_유저_검색_성공_테스트() {
    // given
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw1");
    userRepository.save(user);

    // when
    Optional<User> targetUser = userRepository.findUserByEmail("kwon@example.com");

    // then
    assertThat(targetUser).isPresent();
    assertThat(targetUser.get().getUsername()).isEqualTo("kwon");
  }

  @Test
  void 이메일로_유저_검색_실패_테스트() {
    // given
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw1");
    userRepository.save(user);

    // when
    Optional<User> targetUser = userRepository.findUserByEmail("kim@example.com");

    // then
    assertThat(targetUser).isEmpty();
  }

  @Test
  void 유저_네임_으로_유저_검색_성공_테스트() {
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw1");
    userRepository.save(user);

    // when
    Optional<User> targetUser = userRepository.findUserByUsername("kwon");

    // then
    assertThat(targetUser).isPresent();
    assertThat(targetUser.get().getEmail()).isEqualTo("kwon@example.com");
  }


  @Test
  void 유저_네임_으로_유저_검색_실패_테스트() {
    // given
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw1");
    userRepository.save(user);

    // when
    Optional<User> targetUser = userRepository.findUserByUsername("kim");

    // then
    assertThat(targetUser).isEmpty();
  }
}
