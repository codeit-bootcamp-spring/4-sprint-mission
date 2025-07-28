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
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final UserStatusMapper userStatusMapper;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    validateUsernameDuplicate(request.username());
    validateEmailDuplicate(request.email());

    User user = userMapper.mapToEntity(request);

    optionalProfileCreateRequest.ifPresent(profileCreateRequest -> {
      BinaryContent profile = binaryContentMapper.mapToEntity(profileCreateRequest);
      user.setProfile(profile);
    });

    UserStatus userStatus = userStatusMapper.mapToEntity(user, Instant.now());
    user.setStatus(userStatus);

    return userMapper.mapToDto(userRepository.save(user));
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return userMapper.mapToDto(getFindingUser(userId));
  }

  // !!!!!!!!!!!!!!!! 페이징 구현 필요, Page<UserDto>
  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userMapper.mapToDtoList(userRepository.findAll());
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = getFindingUser(userId);
    validateUsernameDuplicate(request.newUsername());
    validateEmailDuplicate(request.newEmail());

    UserUpdateRequest updateRequest = new UserUpdateRequest(
        request.newUsername(),
        request.newEmail(),
        request.newPassword(),
        optionalProfileCreateRequest.orElse(null)
    );

    User newUser = userMapper.mapToUpdatedEntity(updateRequest);
    if (newUser.getUsername() != null && !Objects.equals(user.getUsername(),
        newUser.getUsername())) {
      user.setUsername(newUser.getUsername());
    }
    if (newUser.getEmail() != null && !Objects.equals(user.getEmail(), newUser.getEmail())) {
      user.setEmail(newUser.getEmail());
    }
    if (newUser.getPassword() != null && !Objects.equals(user.getPassword(),
        newUser.getPassword())) {
      user.setPassword(newUser.getPassword());
    }
    if (!Objects.equals(user.getProfile(), newUser.getProfile())) {
      user.setProfile(newUser.getProfile());
    }

    return userMapper.mapToDto(userRepository.save(user));
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    userRepository.delete(getFindingUser(userId));
  }

  // 공통 메서드
  private User getFindingUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }

  private void validateUsernameDuplicate(String username) {
    if (username != null) {
      userRepository.findByUsername(username)
          .ifPresent(user -> {
            throw new IllegalArgumentException(
                "User with username " + username + " already exists");
          });
    }
  }

  private void validateEmailDuplicate(String email) {
    if (email != null) {
      userRepository.findByEmail(email)
          .ifPresent(user -> {
            throw new IllegalArgumentException("User with email " + email + " already exists");
          });
    }
  }

}
