package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  //
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
                        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    // 1. User 엔티티 생성 (BinaryContentCreateRequest -> BinaryContent 매핑은 Mapper 내부에서)
    User user = userMapper.toEntity(userCreateRequest, optionalProfileCreateRequest.orElse(null));

    // 2. 프로필이 존재한다면 BinaryContent 저장
    BinaryContent savedProfile = Optional.ofNullable(user.getProfile())
            .map(binaryContentRepository::save)
            .orElse(null);

    user.setProfile(savedProfile);

    User savedUser = userRepository.save(user);

    UserStatus userStatus = new UserStatus(savedUser, Instant.now());
    userStatusRepository.save(userStatus);

    return userMapper.toUserDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    User find = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found"));
    return userMapper.toUserDto(find);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    List<User> userList = userRepository.findAll();
    return userList.stream()
            .map(userMapper::toUserDto)
            .toList();
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
  ) {
    //기존 User 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    //중복체크
    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    //프로필 이미지 교체
    BinaryContent newProfile = optionalProfileCreateRequest.map(request -> {
      Optional.ofNullable(user.getProfile())
              .ifPresent(old -> binaryContentRepository.deleteById(old.getId()));

      BinaryContent profile = binaryContentMapper.toEntity(request);
      return binaryContentRepository.save(profile);
    })
    .orElse(user.getProfile());

    userMapper.updateFromDto(userUpdateRequest, user);

    user.updateProfile(newProfile);

    User saved = userRepository.save(user);

    return userMapper.toUserDto(saved);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    Optional.ofNullable(user.getProfile())
        .ifPresent(profile -> binaryContentRepository.deleteById(profile.getId()));

    userStatusRepository.deleteByUserId(userId);

    userRepository.deleteById(userId);
  }
}
