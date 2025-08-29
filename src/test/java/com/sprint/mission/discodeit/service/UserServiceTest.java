package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private UserStatusRepository userStatusRepository;

    @InjectMocks
    private BasicUserService userService;

    @DisplayName("올바른 User 생성 시")
    @Test
    void createUserOk() {
        // given
        UserCreateRequest request = new UserCreateRequest("test", "test", "test@test.com");
        User user = new User(request, null);
        given(userRepository.save(user)).willReturn(user);
        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByUsername(request.username())).willReturn(false);

        // when
        User createdUser = userService.createUser(request, null);

        // then
        assertEquals(request.username(), createdUser.getUsername());
        assertEquals(request.password(), createdUser.getPassword());
        assertEquals(request.email(), createdUser.getEmail());
        then(userRepository).should(times(1)).save(user);
        then(userRepository).should(times(1)).existsByEmail(request.email());
        then(userRepository).should(times(1)).existsByUsername(request.username());
    }

    @DisplayName("이메일이 중복이면 UserAlreadyExistsException 발생")
    @Test
    void createUserShouldFailedWhenDuplicateEmail() {
        // given
        UserCreateRequest request = new UserCreateRequest("test", "test", "test@test.com");
        given(userRepository.existsByEmail(request.email())).willReturn(true);

        // when & then
        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request, null));
        assertEquals("Username Or Email Already Exists", exception.getMessage());
        assertInstanceOf(UserAlreadyExistsException.class, exception);
    }

    @DisplayName("유저이름이 중복이면 UserAlreadyExistsException 발생")
    @Test
    void createUserShouldFailedWhenDuplicateUsername() {
        // given
        UserCreateRequest request = new UserCreateRequest("test", "test", "test@test.com");
        given(userRepository.existsByUsername(request.username())).willReturn(true);

        // when & then
        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request, null));
        assertEquals("Username Or Email Already Exists", exception.getMessage());
        assertInstanceOf(UserAlreadyExistsException.class, exception);
    }

    @DisplayName("유저 수정 성공")
    @Test
    void updateUserOk() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("newUsername", "new@email.com", "newPassword");
        UUID uuid = UUID.randomUUID();
        User testUser = User.of(uuid, "oldUsername", "old@email.com", "oldPassword", null);
        UserStatus testUserStatus = new UserStatus(testUser, Instant.now());
        given(userRepository.findById(uuid)).willReturn(Optional.of(testUser));
        given(userStatusRepository.findByUserId(uuid)).willReturn(testUserStatus);
        given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
        given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
        given(userRepository.save(testUser)).willReturn(testUser);
        given(userStatusRepository.save(testUserStatus)).willReturn(testUserStatus);

        // when
        User user = userService.updateUser(uuid, request, null);

        // then
        then(userRepository).should(times(1)).save(user);
        assertEquals(request.newUsername(), user.getUsername());
        assertEquals(request.newEmail(), user.getEmail());
        assertEquals(request.newPassword(), user.getPassword());
        then(userRepository).should(times(1)).existsByEmail(anyString());
        then(userRepository).should(times(1)).existsByUsername(anyString());
    }

    @DisplayName("id에 해당하는 유저를 찾지 못하는 경우 UserNotFoundException 발생")
    @Test
    void updateUserShouldFailedWhenUserNotFound() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("newUsername", "new@email.com", "newPassword");
        UUID uuid = UUID.randomUUID();
        given(userRepository.findById(uuid)).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(uuid, request, null));
        assertEquals("User Not Found", exception.getMessage());
        assertInstanceOf(UserNotFoundException.class, exception);
    }

    @DisplayName("변경할 Email이 중복일 경우 UserAlreadyExistsException 발생")
    @Test
    void updateUserShouldFailedWhenNewEmailAlreadyExists() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("newUsername", "new@email.com", "newPassword");
        UUID uuid = UUID.randomUUID();
        User user = new User();
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(uuid, request, null));
        assertEquals("Username Or Email Already Exists", exception.getMessage());
        assertInstanceOf(UserAlreadyExistsException.class, exception);
    }

    @DisplayName("유저 삭제 성공")
    @Test
    void deleteUserOk() {
        // given
        UUID uuid = UUID.randomUUID();
        User user = User.of(uuid, "username", "test@email.com", "password", null);
        given(userRepository.findById(uuid)).willReturn(Optional.of(user));

        // when
        userService.deleteUser(uuid);

        // then
        then(userRepository).should(times(1)).delete(user);
    }

    @DisplayName("삭제할 유저가 존재하지 않는 경우")
    @Test
    void deleteUserShouldFailedWhenUserNotFound() {
        // given
        UUID uuid = UUID.randomUUID();
        given(userRepository.findById(notNull(UUID.class))).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(uuid));
        assertEquals("User Not Found", exception.getMessage());
        assertInstanceOf(UserNotFoundException.class, exception);
    }

}
