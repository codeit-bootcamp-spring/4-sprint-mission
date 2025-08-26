package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@DataJpaTest
@EnableJpaAuditing
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Test
  @DisplayName("findAllWithProfileAndStatus 성공 - 프로필과 상태가 있는 유저는 조회된다")
  void findAllWithProfileAndStatus_success() {
    // given
    BinaryContent profile = new BinaryContent("test.png", 1024L, "image/png");
    User user = new User("test", "test@email.com", "testPass", profile);
    UserStatus status = new UserStatus(user, Instant.now());

    userRepository.save(user);

    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).hasSize(1);
    User foundUser = result.get(0);

    assertThat(foundUser.getProfile()).isNotNull();
    assertThat(foundUser.getProfile().getFileName()).isEqualTo("test.png");

    assertThat(foundUser.getStatus()).isNotNull();
    assertThat(foundUser.getStatus().isOnline()).isTrue();
  }

  @Test
  @DisplayName("findAllWithProfileAndStatus - 실패 (저장된 유저 없음)")
  void testFindAllWithProfileAndStatusFail() {
    // given: 저장된 User 없음

    // when: 커스텀 쿼리 호출
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then: 결과 검증
    assertThat(result).isEmpty(); // 결과가 없어야 함
  }

  @Test
  @DisplayName("findAllWithProfileAndStatus - 실패 (status 없음)")
  void testFindAllWithStatusMissing() {
    // given
    BinaryContent profile = new BinaryContent("profile.png", 50L, "image/png");
    User userWithProfileOnly = new User("user2", "user2@example.com", "password123", profile);
    userRepository.save(userWithProfileOnly);

    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("findAllWithProfileAndStatus - 실패 (profile 없음)")
  void testFindAllWithProfileMissing() {
    // given
    User userWithStatusOnly = new User("user3", "user3@example.com", "password123", null);
    UserStatus status = new UserStatus(userWithStatusOnly, Instant.now());
    userRepository.save(userWithStatusOnly);

    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getProfile()).isNull();
    assertThat(result.get(0).getStatus()).isNotNull();
  }
}
