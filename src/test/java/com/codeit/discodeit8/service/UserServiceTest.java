package com.codeit.discodeit8.service;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.codeit.discodeit8.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit8.dto.user_service_dto.UserDto;
import com.codeit.discodeit8.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit8.entity.BinaryContent;
import com.codeit.discodeit8.entity.ChannelType;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.entity.UserStatus;
import com.codeit.discodeit8.exception.user.UserNameEmailDuplicateException;
import com.codeit.discodeit8.exception.user.UserNotFoundException;
import com.codeit.discodeit8.mapper.UserMapper;
import com.codeit.discodeit8.repository.ChannelRepository;
import com.codeit.discodeit8.repository.UserRepository;
import com.codeit.discodeit8.repository.UserStatusRepository;
import com.codeit.discodeit8.service.basic.BasicUserService;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository mockUserRepository;

  @Mock
  private ChannelRepository mockChannelRepository;

  @Mock
  private UserStatusRepository mockUserStatusRepository;

  @Mock
  private UserStatusService userStatusService;

  @Mock
  private BinaryContentService binaryContentService;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService;

  @Test
  void 유저_생성_성공_테스트() throws IOException {
    // given
    UserCreateRequest request = new UserCreateRequest("kwon1", "pw1", "kwon1@email.com", null);
    User mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    mockUser.setUsername(request.getUsername());
    mockUser.setEmail(request.getEmail());
    mockUser.setPassword(request.getPassword());

    given(userMapper.toUser(any(UserCreateRequest.class))).willReturn(mockUser);

    UserStatus mockStatus = new UserStatus();
    mockStatus.setUser(mockUser);
    mockStatus.setId(UUID.randomUUID());
    mockUser.setStatus(mockStatus);

    given(userStatusService.createUserStatus(any(User.class))).willReturn(mockStatus);

    UserDto mockDto = new UserDto(mockUser.getId(), mockUser.getUsername(), mockUser.getEmail(), null, true);
    given(userMapper.toUserDto(any(User.class))).willReturn(mockDto);

    given(mockUserRepository.findUserByUsername("kwon1")).willReturn(Optional.empty());
    given(mockUserRepository.findUserByEmail("kwon1@email.com")).willReturn(Optional.empty());
    given(mockChannelRepository.findAllByType(ChannelType.PUBLIC)).willReturn(List.of());

    // when
    UserDto result = userService.createUser(request);

    // then
    then(mockUserRepository).should().save(any(User.class));
    then(userStatusService).should().createUserStatus(any(User.class));
    then(mockUserStatusRepository).should().save(any(UserStatus.class));
    then(binaryContentService).should().createByteFile(any(), any());
    then(mockChannelRepository).should().findAllByType(ChannelType.PUBLIC);
    assertEquals("kwon1", result.username());
  }

  @Test
  void 유저네임이_중복될_때_유저_생성_실패_테스트(){
    // given
    UserCreateRequest request = new UserCreateRequest("kwon1", "pw1", "kown1@email.com", null);
    given(mockUserRepository.findUserByUsername("kwon1")).willReturn(Optional.of(new User()));

    // when
    Executable action = () -> userService.createUser(request);

    // then
    assertThrows(UserNameEmailDuplicateException.class, action);
  }

  @Test
  void 이메일이_중복될_때_유저_생성_실패_테스트(){
    // given
    UserCreateRequest request = new UserCreateRequest("kwon1", "pw1", "kwon1@email.com", null);
    given(mockUserRepository.findUserByEmail("kwon1@email.com")).willReturn(Optional.of(new User()));

    // when
    Executable action = () -> userService.createUser(request);

    // then
    assertThrows(UserNameEmailDuplicateException.class, action);
  }

  @Test
  void 기본_정보_유저_업데이트_성공_테스트() {
    // given
    UUID userId = UUID.randomUUID();
    User targetUser = new User();
    targetUser.setId(userId);
    targetUser.setUsername("oldUsername");
    targetUser.setEmail("old@email.com");
    targetUser.setPassword("oldPassword");

    UserUpdateRequest request = new UserUpdateRequest(userId, "newUsername", "new@email.com", "newPassword");

    given(mockUserRepository.findById(userId)).willReturn(Optional.of(targetUser));
    given(userMapper.toUserDto(any(User.class))).willAnswer(i -> {
      User u = i.getArgument(0);
      return new UserDto(u.getId(), u.getUsername(), u.getEmail(), null, true);
    });

    // when
    UserDto result = userService.updateUser(request, null, null);

    // then
    then(mockUserRepository).should().save(targetUser);
  }

  @Test
  void 프로필_이미지_정보_유저_업데이트_성공_테스트() {
    // given
    UUID userId = UUID.randomUUID();
    User targetUser = new User();
    targetUser.setId(userId);
    targetUser.setUsername("user");
    targetUser.setEmail("email");
    targetUser.setPassword("pw");

    BinaryContent oldProfile = new BinaryContent();
    oldProfile.setFileName("old.png");
    oldProfile.setSize(100L);
    oldProfile.setContentType("image/png");
    targetUser.setProfile(oldProfile);

    UserUpdateRequest request = new UserUpdateRequest(userId, "user", "email", "pw");

    given(mockUserRepository.findById(userId)).willReturn(Optional.of(targetUser));
    given(userMapper.toUserDto(any(User.class))).willAnswer(i -> {
      User u = i.getArgument(0);
      return new UserDto(u.getId(), u.getUsername(), u.getEmail(), null, true);
    });

    byte[] dummyBytes = new byte[]{1,2,3};

    // when
    UserDto result = userService.updateUser(request, new BinaryContent(), dummyBytes);

    // then
    then(binaryContentService).should().createByteFile(any(), eq(dummyBytes));
    then(mockUserRepository).should(times(2)).save(targetUser);
  }

  @Test
  void 유저_업데이트_할_때_유저가_없어_실패_테스트() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest(userId, "user", "email", "pw");

    given(mockUserRepository.findById(userId)).willReturn(Optional.empty());

    // when
    Executable action = () -> userService.updateUser(request, null, null);

    // then
    assertThrows(UserNotFoundException.class, action);
  }

  @Test
  void 유저_삭제_성공_테스트() {
    // given
    UUID userId = UUID.randomUUID();
    User targetUser = new User();
    targetUser.setId(userId);
    targetUser.setUsername("oldUsername");
    targetUser.setEmail("old@email.com");
    targetUser.setPassword("oldPassword");

    given(mockUserRepository.findById(userId)).willReturn(Optional.of(targetUser));

    // when
    userService.deleteUser(userId);

    // then
    then(mockUserRepository).should().delete(targetUser);
  }

  @Test
  void 유저가_없을_때_유저_삭제_실패_테스트() {
    // given
    UUID userId = UUID.randomUUID();
    given(mockUserRepository.findById(userId)).willReturn(Optional.empty());

    // when
    Executable action = () -> userService.deleteUser(userId);

    // then
    assertThrows(UserNotFoundException.class, action);
  }
}