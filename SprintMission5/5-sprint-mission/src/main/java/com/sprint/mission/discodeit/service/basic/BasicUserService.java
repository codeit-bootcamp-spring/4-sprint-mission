package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.dto.user.UserCreateServiceRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentService basicBinaryContentService;
  private final UserStatusService userStatusService;

  @Override
  public UserDto createUser(UserCreateServiceRequest request) {
    UserEntity newUserEntity = userMapper.toUser(request);
    validateDuplicateUser(newUserEntity);

    UserEntity savedUserEntity = userRepository.save(newUserEntity);

    MultipartFile profileFile = request.profile();
    if (profileFile != null && !profileFile.isEmpty()) {
      BinaryContent createdBinaryContent = basicBinaryContentService.createBinaryContent(
          new BinaryContentCreateServiceRequest(savedUserEntity.getId(), null, profileFile));
      savedUserEntity.setProfileId(createdBinaryContent.id());
      userRepository.save(savedUserEntity);
    }

    userStatusService.createUserStatus(savedUserEntity.getId());
    return userMapper.toUserDto(savedUserEntity);
  }

  @Override
  public UserDto updateUser(UUID userId,
      UserUpdateServiceRequest request) {
    UserEntity userEntity = isExistUser(userId);

    MultipartFile profileFile = request.profile();
    if (profileFile != null && !profileFile.isEmpty()) {
      if (userEntity.getProfileId() != null) {
        basicBinaryContentService.deleteBinaryContent(userEntity.getProfileId());
      }

      BinaryContent updatedBinaryContent =
          basicBinaryContentService.createBinaryContent(
              new BinaryContentCreateServiceRequest(userEntity.getId(), null, profileFile));
      userEntity.setProfileId(updatedBinaryContent.id());

    }

    userMapper.updateFromUserUpdateServiceRequest(userEntity, request);

    if (request.newUsername() != null && !request
        .newUsername().equals(userEntity.getUsername())) {
      validateDuplicateUsername(userEntity);
    }
    if (request.newEmail() != null && !request.newEmail()
        .equals(userEntity.getEmail())) {
      validateDuplicateEmail(userEntity);
    }

    UserEntity updatedUserEntity = userRepository.save(userEntity);
    return userMapper.toUserDto(updatedUserEntity);
  }

  @Override
  public void deleteUser(UUID userId) {
    UserEntity userEntity = isExistUser(userId);
    if (userEntity.getProfileId() != null) {
      basicBinaryContentService.deleteBinaryContent(userEntity.getProfileId());
    }
    userRepository.delete(userEntity.getId());
  }

  @Override
  public List<UserDto> findAllUser() {
    return userRepository.findAll().stream()
        .map(userMapper::toUserDto)
        .collect(Collectors.toList());
  }

  private UserEntity isExistUser(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
  }

  private void validateDuplicateUser(UserEntity userEntity) {
    validateDuplicateUsername(userEntity);
    validateDuplicateEmail(userEntity);
  }

  private void validateDuplicateUsername(UserEntity userEntity) {
    boolean valid =
        userRepository.findAll().stream()
            .anyMatch(
                u -> !u.getId().equals(userEntity.getId()) && u.getUsername()
                    .equals(userEntity.getUsername()));
    if (valid) {
      throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
    }
  }

  private void validateDuplicateEmail(UserEntity userEntity) {
    boolean valid =
        userRepository.findAll().stream()
            .anyMatch(u -> !u.getId().equals(userEntity.getId()) && u.getEmail()
                .equals(userEntity.getEmail()));
    if (valid) {
      throw new IllegalArgumentException("이미 존재하는 사용자 이메일입니다.");
    }
  }

  private boolean getUserStatus(UUID userId) {
    return userStatusRepository.findAll().stream()
        .filter(us -> us.getUserId().equals(userId))
        .findFirst()
        .map(UserStatusEntity::isOnline)
        .orElse(false);
  }

  private byte[] getUserImage(UUID userId) {
    return binaryContentRepository.findAll().stream()
        .filter(binaryContent -> userId.equals(binaryContent.getUserId()))
        .findFirst()
        .map(BinaryContentEntity::getData)
        .orElse(null);
  }
}