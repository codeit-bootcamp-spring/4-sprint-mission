package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidUserArgumentException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BasicUserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private BasicUserService basicUserService;

  @Test
  @DisplayName("유저 생성 성공")
  void create_success() {

    // given
    // 테스트 입력 데이터
    UserCreateRequest request = new UserCreateRequest("testUser", "test@email.com", "password");

    // 반환 설정
    given(userRepository.existsByUsername("testUser")).willReturn(false);
    given(userRepository.existsByEmail("test@email.com")).willReturn(false);

    // save 호출 시 User 반환 (빈 객체 사용)
    given(userRepository.save(any(User.class))).willReturn(new User());

    // 매핑 시 DTO 생성
    given(userMapper.toDto(any(User.class)))
        .willAnswer(
            invocation -> {
              User userArg = invocation.getArgument(0);
              return new UserDto(
                  userArg.getId(), userArg.getUsername(), userArg.getEmail(), null, null);
            });

    // when
    // 테스트 대상 메서드 호출
    UserDto userDto = basicUserService.create(request, Optional.empty());

    // then
    // 검증
    assertThat(userDto).isNotNull();
    verify(userRepository).existsByEmail(request.email());
    verify(userRepository).existsByUsername(request.username());
    verify(userRepository).save(any(User.class));
    verify(userMapper).toDto(any(User.class));
  }

  @Test
  @DisplayName("유저 생성 실패 - 이미 존재하는 이메일을 사용할 경우, 회원 가입에 실패한다")
  void create_fail_emailExists() {
    // given
    // 중복된 이메일
    UserCreateRequest request = new UserCreateRequest("testUser", "test@email.com", "password");
    given(userRepository.existsByEmail(request.email())).willReturn(true);

    // when & then
    // 메서드 호출 시 UserAlreadyExistsException 발생
    assertThatThrownBy(() -> basicUserService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);

    // 검증
    verify(userRepository).existsByEmail(request.email());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("유저 생성 실패 - 이미 존재하는 유저 이름을 사용할 경우, 회원 가입에 실패한다")
  void create_fail_usernameExists() {
    // given
    // 중복된 유저 이름
    UserCreateRequest request = new UserCreateRequest("testUser", "test@email.com", "password");
    given(userRepository.existsByUsername(request.username())).willReturn(true);

    assertThatThrownBy(() -> basicUserService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);

    verify(userRepository).existsByUsername(request.username());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("유저 정보 수정 성공")
  void update_success() {
    // given
    UUID uuid = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUser", "new@email.com", "newPassword");

    // 기존 User (빈 객체 사용)
    User existingUser = new User();

    given(userRepository.findById(uuid)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(false);

    // 매핑 시 DTO 생성
    given(userMapper.toDto(existingUser))
        .willReturn(
            new UserDto(
                existingUser.getId(), request.newUsername(), request.newEmail(), null, null));

    // when
    UserDto updatedDto = basicUserService.update(uuid, request, Optional.empty());

    // then
    // 검증
    assertThat(updatedDto).isNotNull();
    verify(userRepository).findById(uuid);
    verify(userRepository).existsByEmail(request.newEmail());
    verify(userRepository).existsByUsername(request.newUsername());
    verify(userMapper).toDto(existingUser);
  }

  @Test
  @DisplayName("유저 정보 수정 실패 - 존재하지 않는 유저의 정보는 수정할 수 없다")
  void update_fail_userNotFound() {
    // given
    UUID uuid = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUser", "new@email.com", "newPassword");

    given(userRepository.findById(uuid)).willReturn(Optional.empty());

    // when & then
    // 메서드 호출 시 UserNotFoundException 발생
    assertThatThrownBy(() -> basicUserService.update(uuid, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).findById(uuid);

    // User가 없으므로 이후 로직은 호출되면 안 됨
    verify(userRepository, never()).existsByEmail(request.newEmail());
    verify(userRepository, never()).existsByUsername(request.newUsername());
    verify(userMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("유저 정보 수정 실패 - 이메일 또는 이름이 null이면 오류 발생")
  void update_fail_emailOrUsernameIsNull() {
    // given
    UUID uuid = UUID.randomUUID();
    User existingUser = new User();
    given(userRepository.findById(uuid)).willReturn(Optional.of(existingUser));

    // 이메일 null
    UserUpdateRequest request1 = new UserUpdateRequest("newUser", null, "newPassword");
    // when & then
    assertThatThrownBy(() -> basicUserService.update(uuid, request1, Optional.empty()))
        .isInstanceOf(InvalidUserArgumentException.class);

    // 이름 null
    UserUpdateRequest request2 = new UserUpdateRequest(null, "new@email.com", "newPassword");
    // when & then
    assertThatThrownBy(() -> basicUserService.update(uuid, request2, Optional.empty()))
        .isInstanceOf(InvalidUserArgumentException.class);

    // repository 메서드 호출되지 않아야 함
    verify(userRepository, times(2)).findById(uuid);
    verify(userRepository, never()).save(any());
    verify(userMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("유저 정보 수정 실패 - 이메일이 이미 존재하면 오류 발생")
  void update_fail_emailAlreadyExists() {
    // given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail("existing@email.com")).willReturn(true);

    UserUpdateRequest request =
        new UserUpdateRequest("newUsername", "existing@email.com", "newPassword");

    // when & then
    assertThatThrownBy(() -> basicUserService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);

    verify(userRepository).findById(userId);
    verify(userRepository).existsByEmail("existing@email.com");
    verify(userMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("유저 정보 수정 실패 - 이름이 이미 존재하면 오류 발생")
  void update_fail_usernameAlreadyExists() {
    // given
    UUID userId = UUID.randomUUID();
    User existingUser = new User();
    given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByUsername("existingUsername")).willReturn(true);

    UserUpdateRequest request =
        new UserUpdateRequest("existingUsername", "new@email.com", "newPassword");

    // when & then
    assertThatThrownBy(() -> basicUserService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);

    verify(userRepository).findById(userId);
    verify(userRepository).existsByUsername("existingUsername");
    verify(userMapper, never()).toDto(any());
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void delete_success() {
    // given
    UUID uuid = UUID.randomUUID();

    given(userRepository.existsById(uuid)).willReturn(true);
    willDoNothing().given(userRepository).deleteById(uuid);

    // when
    basicUserService.delete(uuid);

    // then
    // 검증
    verify(userRepository).existsById(uuid);
    verify(userRepository).deleteById(uuid);
  }

  @Test
  @DisplayName("유저 삭제 실패 - 존재하지 않는 유저는 삭제할 수 없다")
  void delete_fail_userNotFound() {
    // given
    UUID uuid = UUID.randomUUID();

    given(userRepository.existsById(uuid)).willReturn(false);

    // when & then
    // 메서드 호출 시 UserNotFoundException 발생
    assertThatThrownBy(() -> basicUserService.delete(uuid))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).existsById(uuid);

    // 호출되면 안 됨
    verify(userRepository, never()).deleteById(any());
  }
}
