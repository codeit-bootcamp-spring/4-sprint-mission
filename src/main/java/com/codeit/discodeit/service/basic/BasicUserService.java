package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.user_service_dto.*;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.user.UserNameEmailDuplicateException;
import com.codeit.discodeit.exception.user.UserNotFoundException;
import com.codeit.discodeit.mapper.BinaryContentMapper;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.service.ReadStatusService;
import com.codeit.discodeit.service.UserService;
import com.codeit.discodeit.service.UserStatusService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusService readStatusService;
  private final UserStatusService userStatusService;
  private final BinaryContentService binaryContentService;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDto createUser(UserCreateRequest userCreateRequest) throws IOException {
    log.info("[createUser] 사용자 생성 요청: username={}, email={}",
        userCreateRequest.getUsername(), userCreateRequest.getEmail());

    validateUserNameNotDuplicated(userCreateRequest.getUsername());
    validateUserEmailNotDuplicated(userCreateRequest.getEmail());

    byte[] profileImgBytes;
    MultipartFile profile = userCreateRequest.getProfileImage();
    if (profile == null || profile.isEmpty()) {
      log.info("[createUser] 프로필 이미지 없음 → 기본 이미지 적용");
      profileImgBytes = BinaryContentMapper.getBasicProfileBytes();
    } else {
      log.info("[createUser] 프로필 이미지 업로드됨: fileName={}, size={}",
          profile.getOriginalFilename(), profile.getSize());
      profileImgBytes = profile.getBytes();
    }

    User user = userMapper.toUser(userCreateRequest);
    userRepository.save(user);
    binaryContentService.createByteFile(user.getProfile(), profileImgBytes);

    UserStatus userStatus = userStatusService.createUserStatus(user);
    user.setStatus(userStatus);
    userStatusRepository.save(userStatus);

    // 공용 채널 readStatus 추가
    List<Channel> channels = channelRepository.findAllByType(ChannelType.PUBLIC);
    log.info("[createUser] 공용 채널 {}개에 readStatus 등록", channels.size());
    for (Channel channel : channels) {
      readStatusService.createReadStatus(user, channel);
    }

    UserDto result = userMapper.toUserDto(user);
    log.info("[createUser] 사용자 생성 완료: userId={}", result.id());
    return result;
  }

  @Override
  @Transactional
  public UserDto updateUser(UserUpdateRequest userUpdateRequest,
      BinaryContent newProfileImage,
      byte[] profileImgBytes) {
    log.info("[updateUser] 사용자 수정 요청: userId={}", userUpdateRequest.getUserId());

    User targetUser = findUserByUserId(userUpdateRequest.getUserId());

    // 이메일 변경
    if (userUpdateRequest.getNewEmail() != null &&
        !userUpdateRequest.getNewEmail().equals(targetUser.getEmail())) {
      log.info("[updateUser] 이메일 변경: {} → {}", targetUser.getEmail(), userUpdateRequest.getNewEmail());
      validateUserEmailNotDuplicated(userUpdateRequest.getNewEmail());
      targetUser.setEmail(userUpdateRequest.getNewEmail());
    }

    // 사용자명 변경
    if (userUpdateRequest.getNewUsername() != null &&
        !userUpdateRequest.getNewUsername().equals(targetUser.getUsername())) {
      log.info("[updateUser] 사용자명 변경: {} → {}", targetUser.getUsername(), userUpdateRequest.getNewUsername());
      validateUserNameNotDuplicated(userUpdateRequest.getNewUsername());
      targetUser.setUsername(userUpdateRequest.getNewUsername());
    }

    // 비밀번호 변경
    if (userUpdateRequest.getNewPassword() != null &&
        !userUpdateRequest.getNewPassword().equals(targetUser.getPassword())) {
      log.info("[updateUser] 비밀번호 변경");
      targetUser.setPassword(userUpdateRequest.getNewPassword());
    }

    // 프로필 이미지 변경
    if (newProfileImage != null) {
      BinaryContent oldProfileImg = targetUser.getProfile();

      boolean isSameImage = oldProfileImg != null
          && oldProfileImg.getSize() == newProfileImage.getSize()
          && oldProfileImg.getContentType().equals(newProfileImage.getContentType())
          && oldProfileImg.getFileName().equals(newProfileImage.getFileName());

      if (!isSameImage) {
        log.info("[updateUser] 프로필 이미지 변경됨: {} → {}",
            oldProfileImg != null ? oldProfileImg.getFileName() : "기본 이미지",
            newProfileImage.getFileName());
        targetUser.setProfile(newProfileImage);
        userRepository.save(targetUser);
        binaryContentService.createByteFile(targetUser.getProfile(), profileImgBytes);
      } else {
        log.info("[updateUser] 동일한 프로필 이미지 → 변경 생략");
      }
    }

    userRepository.save(targetUser);
    UserDto result = userMapper.toUserDto(targetUser);
    log.info("[updateUser] 사용자 수정 완료: userId={}", result.id());
    return result;
  }

  @Override
  @Transactional
  public void deleteUser(UUID userId) {
    log.info("[deleteUser] 사용자 삭제 요청: userId={}", userId);
    User user = findUserByUserId(userId);
    userRepository.delete(user);
    log.info("[deleteUser] 사용자 삭제 완료: userId={}", userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAllUser() {
    List<User> userList = userRepository.findAll();
    return userList.stream().map(userMapper::toUserDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findUserDtoByUserId(UUID userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      Map<String, Object> details = Map.of(
          "이유", "유저 없음"
      );
      throw new UserNotFoundException(details);
    }
    return userMapper.toUserDto(user.get());
  }

  @Override
  @Transactional(readOnly = true)
  public User findUserByUserId(UUID userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      Map<String, Object> details = Map.of(
          "이유", "유저 없음"
      );
      throw new UserNotFoundException(details);
    }
    return user.get();
  }

  private void validateUserNameNotDuplicated(String userName) {
    log.info("[validateUserNameNotDuplicated] 유저 네임 중복 확인 시작: userName={}", userName);
    if (userRepository.findUserByUsername(userName).isPresent()) {
      log.debug("[validateUserNameNotDuplicated] 유저 네임 중복이 발견: userName={}", userName);
      Map<String, Object> details = Map.of(
          "이유", "중복"
      );
      throw new UserNameEmailDuplicateException(details);
    }
    log.info("[validateUserNameNotDuplicated] 유저 네임 중복 확인 종료: userName={}", userName);
  }

  private void validateUserEmailNotDuplicated(String userEmail) {
    log.info("[validateUserEmailNotDuplicated] 유저 이메일 중복 확인 시작: userEmail={}", userEmail);
    if (userRepository.findUserByEmail(userEmail).isPresent()) {
      log.debug("[validateUserEmailNotDuplicated] 유저 이메일 중복이 발견: userEmail={}", userEmail);
      Map<String, Object> details = Map.of(
          "이유", "중복"
      );
      throw new UserNameEmailDuplicateException(details);
    }
    log.info("[validateUserEmailNotDuplicated] 유저 이메일 중복 확인 종료: userEmail={}", userEmail);
  }
}