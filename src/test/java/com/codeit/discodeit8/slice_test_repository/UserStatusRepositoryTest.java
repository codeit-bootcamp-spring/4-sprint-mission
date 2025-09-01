package com.codeit.discodeit8.slice_test_repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.discodeit8.auditing_config.AuditingConfig;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.entity.UserStatus;
import com.codeit.discodeit8.repository.UserRepository;
import com.codeit.discodeit8.repository.UserStatusRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test") // yaml 파일을 test로
@Import(AuditingConfig.class)
class UserStatusRepositoryTest {

  @Autowired
  private UserStatusRepository userStatusRepository;

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

    UserStatus userStatus = new UserStatus();
    userStatus.setUser(user);
    userStatusRepository.save(userStatus);

    // when
    Optional<UserStatus> targetUserStatus = userStatusRepository.findByUserId(user.getId());

    // then
    assertThat(targetUserStatus).isPresent();
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
    Optional<UserStatus> targetUserStatus = userStatusRepository.findByUserId(user.getId());

    // then
    assertThat(targetUserStatus).isEmpty();
  }
}
