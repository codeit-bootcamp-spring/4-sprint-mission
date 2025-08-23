package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @InjectMocks
    private BasicUserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("사용자 가입 성공")
    void user_create_success() {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "username",
                "email@codeit.com",
                "password"
        );
        // 중복 검사는 모두 통과한다 치고
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        UserDto expectDto = new UserDto(
                UUID.randomUUID(),
                "username",
                "email@codeit.com",
                null,
                true
        );
        when(userMapper.toDto(any(User.class))).thenReturn(expectDto);

        // when : 회원 가입
        // 질문) 여기서는 프로필 이미지 없다치고, 만약 프로필 이미지도 검증하고 싶으면 다른 테스트로 빼야하는거 맞나요?
        UserDto result = userService.create(request, Optional.empty());

        // then
        assertAll(
                "회원 가입 정보 검증",
                () -> assertEquals(expectDto.id(), result.id()),
                () -> assertEquals(expectDto.username(), result.username()),
                () -> assertEquals(expectDto.email(), result.email()),
                () -> assertEquals(expectDto.profile(), result.profile()),
                () -> assertEquals(expectDto.online(), result.online())
        );
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("중복 이메일로 인한 회원가입 실패")
    void user_create_fail_duplicate_email() {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "username",
                "email@codeit.com",
                "password"
        );
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        // 중복된 이메일 존재
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThrows(UserEmailAlreadyExistsException.class, () -> userService.create(request, Optional.empty()));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void user_update_success() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(
                "userUserName",
                "newEmail@codeit,com",
                "newPassword"
        );
        // 유저 준비
        User user = mock(User.class);
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // 중복 체크 통과
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        // 예상 DTO
        UserDto expectDto = new UserDto(
                userId,
                request.newUsername(),
                request.newEmail(),
                null,
                true
        );
        // 예상 DTO 반환
        when(userMapper.toDto(any(User.class))).thenReturn(expectDto);

        // when
        UserDto result = userService.update(userId, request, Optional.empty());

        // then
        assertAll(
                () -> assertEquals(expectDto.id(), result.id()),
                () -> assertEquals(expectDto.username(), result.username()),
                () -> assertEquals(expectDto.email(), result.email()),
                () -> assertEquals(expectDto.profile(), result.profile()),
                () -> assertEquals(expectDto.online(), result.online())
        );
    }

    @Test
    @DisplayName("해당 회원이 존재하지 않아서 업데이트 실패")
    void user_update_fail_user_not_found() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(
                "userUserName",
                "newEmail@codeit,com",
                "newPassword"
        );
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(UserNotFoundException.class, () -> userService.update(userId, request, Optional.empty()));

    }


    @Test
    @DisplayName("사용자 삭제 성공")
    void user_delete_success() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);

        // when
        userService.delete(userId);

        // then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("해당 회원이 존재하지 않아 삭제 실패")
    void user_delete_fail_user_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        // when
        // then
        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
        verify(userRepository, never()).deleteById(any());
    }
}