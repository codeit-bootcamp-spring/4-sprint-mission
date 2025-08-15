package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @InjectMocks
  private BasicUserService userService;

  /**
   * 생성 - create
   */
  @DisplayName("유저 생성 테스트")
  @Test
  void createUserTest() {
    // =============== given ===============
    UUID binaryContentId = UUID.randomUUID();
    String username = "Bob";
    String email = "bob@gmail.com";
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        username,
        email,
        "pw1234"
    );

    byte[] fileBytes = "test".getBytes();
    BinaryContentCreateRequest profile = new BinaryContentCreateRequest(
        "profile.png",
        "image/png",
        fileBytes
    );

    // Mock dependencies
    BinaryContent mockSavedBinaryContent = mock(BinaryContent.class);
    User expectedUser = mock(User.class);
    UserDto expectedDto = mock(UserDto.class);

    // Setup mock behaviors
    when(userRepository.existsByUsernameOrEmail(username, email)).thenReturn(false);
    when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(mockSavedBinaryContent);
    when(mockSavedBinaryContent.getId()).thenReturn(binaryContentId);
    when(userRepository.save(any(User.class))).thenReturn(expectedUser);
    when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

    // =============== when ===============
    UserDto actualDto = userService.create(userCreateRequest, Optional.of(profile));

    // =============== then ===============
    assertEquals(expectedDto, actualDto);
    verify(userRepository).existsByUsernameOrEmail(username, email);
    verify(binaryContentRepository).save(any(BinaryContent.class));
    verify(mockSavedBinaryContent).getId();
    verify(binaryContentStorage).put(binaryContentId, fileBytes);
    verify(userRepository).save(any(User.class));
    verify(userMapper).toDto(any(User.class));
  }

  @DisplayName("유저 생성 실패 - 중복된 사용자명 ")
  @Test
  void createUser_Failure_DuplicateUsername() {
    // =============== given ===============
    UUID userId = UUID.randomUUID();
    String username = "Charlie";
    String email = "charlie@gmail.com";

    UserCreateRequest userCreateRequest = new UserCreateRequest(
        username,
        email,
        "pw9999"
    );

    // Mock that user already exists
    when(userRepository.existsByUsernameOrEmail(username, email)).thenReturn(true);
    when(userRepository.findByUsername(username)).thenThrow(new DuplicateUsernameException(userId));
    // =============== when & then ===============
    assertThrows(DuplicateUsernameException.class, () -> {
      userService.create(userCreateRequest, Optional.empty());
    });

    verify(userRepository).existsByUsernameOrEmail(username, email);
    verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    verify(userRepository, never()).save(any(User.class));
  }

  @DisplayName("유저 생성 실패 - 중복된 이매일 ")
  @Test
  void createUser_Failure_DuplicateEmail() {
    // =============== given ===============
    UUID userId = UUID.randomUUID();
    String username = "Charlie";
    String email = "charlie@gmail.com";

    UserCreateRequest userCreateRequest = new UserCreateRequest(
        username,
        email,
        "pw9999"
    );

    // Mock that user already exists
    when(userRepository.existsByUsernameOrEmail(username, email)).thenReturn(true);
    when(userRepository.findByEmail(email)).thenThrow(new DuplicateEmailException(userId));
    // =============== when & then ===============
    assertThrows(DuplicateEmailException.class, () -> {
      userService.create(userCreateRequest, Optional.empty());
    });

    verify(userRepository).existsByUsernameOrEmail(username, email);
    verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    verify(userRepository, never()).save(any(User.class));
  }

  /**
   * 수정 - update
   */
  @DisplayName("유저 수정 테스트")
  @Test
  void updateUserTest() {
    // ============ given ============
    UUID userId = UUID.randomUUID();
    User user = spy(new User(
        "oldUsername",
        "oldEmail",
        "oldPassword",
        null
    ));
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "newUsername",
        "newEmail",
        "newPassword"
    );
    UUID binaryContentId = UUID.randomUUID();
    byte[] fileBytes = "test".getBytes();
    BinaryContentCreateRequest profile = new BinaryContentCreateRequest(
        "profile.png",
        "image/png",
        fileBytes
    );
    BinaryContent savedBinaryContent = spy(new BinaryContent(
        "profile.png",
        (long) fileBytes.length,
        "image/png"
    ));

    UserDto expectedDto = new UserDto(
        userId,
        "newUsername",
        "newEmail",
        null, // or mock BinaryContentDto if needed
        null  // or mock UserStatusDto if needed
    );

    // Mock dependencies
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(
        savedBinaryContent);

    // ============ when ============
    userService.update(userId, userUpdateRequest, Optional.of(profile));

    // ============ then ============
    verify(userRepository).findById(userId);
    verify(binaryContentRepository).save(any(BinaryContent.class));
  }

  @DisplayName("유저 수정 테스트 실패 - 유저 존재하지 않음")
  @Test
  void updateUser_Failure_UserDoesNotExist() {
    // =============== given ===============

    UUID userId = UUID.randomUUID();
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "newUsername",
        "newEmail",
        "newPassword"
    );

    // =============== when ===============
    assertThrows(UserNotFoundException.class,
        () -> userService.update(userId, userUpdateRequest, Optional.empty()));

    // =============== then ===============
    verify(binaryContentRepository, never()).save(any(BinaryContent.class));
    verify(userRepository, never()).deleteById(userId);
    verify(userMapper, never()).toDto(any(User.class));
  }

  @DisplayName("유저 삭제 테스트 실패 - 데이터베이스 실패")
  @Test
  void updateUser_Failure_DBConnection() {
    // ============ given ============
    UUID userId = UUID.randomUUID();
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "newUsername",
        "newEmail",
        "newPassword"
    );
    byte[] fileBytes = "test".getBytes();
    BinaryContentCreateRequest profile = new BinaryContentCreateRequest(
        "profile.png",
        "image/png",
        fileBytes
    );

    // Mock dependencies
    User mockUser = mock(User.class);
    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    doThrow(new DataAccessException("DB connection lost") {
    }).when(binaryContentRepository).save(any(BinaryContent.class));

    // ============ when ============
    assertThrows(DataAccessException.class,
        () -> userService.update(userId, userUpdateRequest, Optional.of(profile)));

    // ============ then ============
    verify(userRepository, times(1)).findById(userId);
    verify(binaryContentRepository, times(1)).save(any(BinaryContent.class));
  }

  /**
   * 삭제 - delete
   */
  @DisplayName("유저 삭제 테스트")
  @Test
  void deleteUserTest() {
    // =============== given ===============
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId)).thenReturn(true);

    // =============== when ===============
    userService.delete(userId);

    // =============== then ===============
    verify(userRepository, times(1)).existsById(userId);
    verify(userRepository, times(1)).deleteById(userId);
  }

  @DisplayName("유저 삭제 테스트 실패 - 유저 존재하지 않음")
  @Test
  void deleteUser_Failure_MessageDoesNotExist() {
    // =============== given ===============
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId)).thenReturn(false);

    // =============== when ===============
    assertThrows(UserNotFoundException.class,
        () -> userService.delete(userId));

    // =============== then ===============
    verify(userRepository, never()).deleteById(userId);
  }


  @DisplayName("유저 삭제 테스트 실패 - 데이터베이스 실패")
  @Test
  void deleteUser_Failure_DBConnection() {
    // ============ given ============
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId)).thenReturn(true);
    doThrow(new DataAccessException("DB connection failed") {
    })
        .when(userRepository).deleteById(userId);

    // ============ when ============
    assertThrows(DataAccessException.class,
        () -> userService.delete(userId));

    // ============ then ============
    verify(userRepository, times(1)).existsById(userId);
    verify(userRepository, times(1)).deleteById(userId);
  }

}
