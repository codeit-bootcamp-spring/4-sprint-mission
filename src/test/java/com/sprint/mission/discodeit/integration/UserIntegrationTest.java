package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.*;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  /**
   * POST - create
   */
  @DisplayName("유저 생성과 저장 테스트")
  @Test
  void CreateUserAndSavesUser() {
    // ================== given ==================
    String username = "Bob";
    String email = "bob@gmail.com";
    UserCreateRequest userCreateRequestDto = new UserCreateRequest(
        username,
        email,
        "pw123"
    );

    // ================== when ==================
    UserDto userDto = userService.create(userCreateRequestDto, Optional.empty());

    // ================== then ==================
    User savedUser = userRepository.findById(userDto.id()).orElseThrow();
    assertThat(savedUser.getUsername()).isEqualTo(username);
    assertThat(savedUser.getEmail()).isEqualTo(email);
  }

  @DisplayName("유저 생성과 저장 실패 - 유저네임 중복")
  @Test
  void CreateUserAndSavesUser_DuplicateUsername_Failure() {
    // ================== given ==================
    String username = "Bob";
    String email = "bob@gmail.com";
    userRepository.save(new User(
            username,
            email,
            "pw123",
            null
        )
    );
    UserCreateRequest userCreateRequestDto = new UserCreateRequest(
        username,
        "email",
        "pw123"
    );
    // ================== when & then ==================
    assertThatThrownBy(() -> userService.create(userCreateRequestDto, Optional.empty()))
        .isInstanceOf(
            DuplicateUsernameException.class); // Or a more specific custom exception
  }

  @DisplayName("유저 생성과 저장 실패 - 이매일 중복")
  @Test
  void CreateUserAndSavesUser_DuplicateEmail_Failure() {
    // ================== given ==================
    String username = "Bob";
    String email = "bob@gmail.com";
    userRepository.save(new User(
            username,
            email,
            "pw123",
            null
        )
    );
    UserCreateRequest userCreateRequestDto = new UserCreateRequest(
        "username",
        email,
        "pw123"
    );
    // ================== when & then ==================
    assertThatThrownBy(() -> userService.create(userCreateRequestDto, Optional.empty()))
        .isInstanceOf(
            DuplicateEmailException.class); // Or a more specific custom exception
  }

  /**
   * PATCH - update
   */
  @DisplayName("유저 수정과 저장 테스트")
  @Test
  void updateUserAndSavesUser() {
    // ================== given ==================
    // creating & saving the user we will update
    UserCreateRequest userCreateRequestDto = new UserCreateRequest(
        "Bob",
        "bob@gmail.com",
        "pw123"
    );
    UserDto userDto = userService.create(userCreateRequestDto, Optional.empty());

    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "new username",
        "new email",
        "new password"
    );

    // ================== when ==================
    userService.update(userDto.id(), userUpdateRequest, Optional.empty());

    // ================== then ==================
    User updatedUser = userRepository.findById(userDto.id()).orElseThrow();
    assertThat(updatedUser.getUsername()).isEqualTo("new username");
    assertThat(updatedUser.getEmail()).isEqualTo("new email");
    assertThat(updatedUser.getPassword()).isEqualTo("new password");
  }

  @DisplayName("유저 수정과 저장 실패 - 유저 존재하지 않음")
  @Test
  void updateUserAndSavesUser_UserDoesNotExist_Failure() {
    // ================== given ==================
    UUID nonExistentUserId = UUID.randomUUID();
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "new username",
        "new email",
        "new password"
    );

    // ================== when & then ==================
    assertThatThrownBy(
        () -> userService.update(nonExistentUserId, userUpdateRequest, Optional.empty()))
        .isInstanceOf(
            UserNotFoundException.class); // Or a more specific custom exception
  }

  /**
   * DELETE
   */
  @DisplayName("유저 삭제 테스트")
  @Test
  void deleteUser() {
    // ================== given ==================
    // create & save channel that we will edit
    UserCreateRequest userCreateRequestDto = new UserCreateRequest(
        "Bob",
        "bob@gmail.com",
        "pw123"
    );
    UserDto userDto = userService.create(userCreateRequestDto, Optional.empty());
    // ================== when ==================

    userService.delete(userDto.id());

    // ================== then ==================
    Optional<User> deletedUser = userRepository.findById(userDto.id());
    assertThat(deletedUser).isEmpty();
  }

  @DisplayName("유저 삭제 실패 - 유저 존재하지 않음")
  @Test
  void deleteUser_UserNotFound_Failure() {
    // ================== given ==================
    UUID nonExistentUserId = UUID.randomUUID();

    // ================== when & then ==================
    assertThatThrownBy(() -> userService.delete(nonExistentUserId))
        .isInstanceOf(UserNotFoundException.class);
  }

  /**
   * GET - findAll
   */
  @DisplayName("모든 유저 조회 테스트")
  @Test
  void findAllUsers() {
    // ================== given ==================

    UserCreateRequest userCreateRequestDto1 = new UserCreateRequest(
        "Bob",
        "bob@gmail.com",
        "pw123"
    );
    UserCreateRequest userCreateRequestDto2 = new UserCreateRequest(
        "Alice",
        "alice@gmail.com",
        "pw123"
    );
    userService.create(userCreateRequestDto1, Optional.empty());
    userService.create(userCreateRequestDto2, Optional.empty());

    // ================== when ==================
    List<UserDto> userDtoList = userService.findAll();

    // ================== then ==================
    assertThat(userDtoList).hasSize(2);
    assertThat(userDtoList)
        .extracting("username")
        .containsExactlyInAnyOrder("Bob", "Alice");
  }

  @DisplayName("모든 유저 조회 테스트 - 유저 없음")
  @Test
  void findAllUsers_NoUsers() {
    // ================== when ==================
    List<UserDto> userDtoList = userService.findAll();

    // ================== then ==================
    assertThat(userDtoList).hasSize(0);
  }

}
