package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserIntegrationTest {

  @Autowired private UserService userService;

  @Autowired private UserRepository userRepository;

  @Test
  @DisplayName("User 생성 성공")
  void createUser_success() {
    // given
    UserCreateRequest request =
        new UserCreateRequest("testuser", "test@example.com", "password123");
    UserDto createdUser = userService.create(request, Optional.empty());

    // when & then
    assertThat(createdUser).isNotNull();
    assertThat(createdUser.username()).isEqualTo("testuser");
    assertThat(createdUser.email()).isEqualTo("test@example.com");
    assertThat(createdUser.online()).isNotNull();
  }

  @Test
  @DisplayName("User 수정 성공")
  void updateUser_success() {
    // given
    UserDto createdUser =
        userService.create(
            new UserCreateRequest("testuser", "test@example.com", "password123"), Optional.empty());

    // when
    UserUpdateRequest updateRequest =
        new UserUpdateRequest("updatedUser", "updated@example.com", "newPass");
    UserDto updatedUser = userService.update(createdUser.id(), updateRequest, Optional.empty());

    // then
    assertThat(updatedUser.username()).isEqualTo("updatedUser");
    assertThat(updatedUser.email()).isEqualTo("updated@example.com");
    assertThat(updatedUser.online()).isNotNull();
  }

  @Test
  @DisplayName("User 삭제 성공")
  void deleteUser_success() {
    // given
    UserDto createdUser =
        userService.create(
            new UserCreateRequest("testuser", "test@example.com", "password123"), Optional.empty());
    UUID userId = createdUser.id();

    // when
    userService.delete(userId);

    // then
    assertThat(userRepository.existsById(userId)).isFalse();
  }

  @Test
  @DisplayName("User 전체 조회")
  void findAllUser_success() {
    // given
    userService.create(
        new UserCreateRequest("testuser", "test@example.com", "password123"), Optional.empty());

    // when
    List<UserDto> users = userService.findAll();

    // then
    assertThat(users).isNotEmpty();
    assertThat(users.get(0).username()).isEqualTo("testuser");
  }
}
