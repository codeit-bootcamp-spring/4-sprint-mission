package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserEmailDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService; // UserService 구현체

  @DisplayName("UserService.create() - 유저 생성 단위 테스트")
  @Test
  void user_create() {
      // given
      UserCreateRequest request = new UserCreateRequest(
          "eunhyeok",
          "eunhyeok@gmail.com",
          "1234"
      );

      //중복 체크
      when(userRepository.existsByUsername(anyString())).thenReturn(false);
      when(userRepository.existsByEmail(anyString())).thenReturn(false);

      UserDto userDto = new UserDto(
          UUID.randomUUID(),
          "eunhyeok",
          "eunhyeok@gmail.com",
          null,
          true
      );
      when(userMapper.toDto(any(User.class))).thenReturn(userDto);

      // when
      UserDto result = userService.create(request, Optional.empty());

      // then
      assertAll(
          "회원 가입",
          () -> assertEquals(userDto.id(), result.id()),
          () -> assertEquals(userDto.username(), result.username()),
          () -> assertEquals(userDto.email(), result.email()),
          () -> assertEquals(userDto.profile(), result.profile()),
          () -> assertEquals(userDto.online(), result.online())
      );
      verify(userRepository, times(1)).save(any(User.class));
    }

  @Test
  @DisplayName("유저 생성 실패")
  void user_create_fail(){

    //given
    UserCreateRequest request = new UserCreateRequest(
        "eunhyeok",
        "eunhyeok@gmail.com",
        "1234"
    );

    //이메일 중복
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    assertThrows(UserEmailDuplicateException.class, () -> userService.create(request, Optional.empty()));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("UserService.Update() - 유저 정보 수정")
  void user_update() {

    //given
    UserUpdateRequest request = new UserUpdateRequest(
        "eunhyeok1",
        "eunhyeok@naver.com",
        "33221"
    );

    //기존 유저 준비
    User user = mock(User.class);
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    //중복 체크 통과로 침
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);

    UserDto userDto = new UserDto(
        userId,
        request.newUsername(),
        request.newEmail(),
        null,
        true
    );
    //DTO 반환
    when(userMapper.toDto(any(User.class))).thenReturn(userDto);

    //when
    UserDto result = userService.update(userId, request, Optional.empty());

    //then
    assertAll(
        () -> assertEquals(userDto.id(), result.id()),
        () -> assertEquals(userDto.username(), result.username()),
        () -> assertEquals(userDto.email(), result.email()),
        () -> assertEquals(userDto.profile(), result.profile()),
        () -> assertEquals(userDto.online(), result.online())
    );
  }

  @Test
  @DisplayName("유저를 찾을 수 없어서 업데이트 실패")
  void user_update_fail() {
    // given
    UserUpdateRequest request = new UserUpdateRequest(
        "eunhyeok",
        "eunhyeok@gmail.com",
        "1234"
    );
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
    () -> userService.update(userId, request, Optional.empty()));
  }

  @Test
  @DisplayName("UserService.delete() - 유저 삭제")
  void user_delete() {
    //given
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId)).thenReturn(true);

    //when
    userService.delete(userId);

    //then
    verify(userRepository, times(1)).deleteById(userId);
  }

  @Test
  @DisplayName("유저를 찾을 수 없어서 삭제 실패")
  void delete_fail(){
    //given
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId)).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
    verify(userRepository, never()).deleteById(userId);
  }

}