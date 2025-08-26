package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.InvalidUserArgumentException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public UserDto create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.info(
        "[UserService] Create started - username: {}, email: {}",
        userCreateRequest.username(),
        userCreateRequest.email());
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      log.warn("[UserService] Create failed - email already exists: {}", email);
      throw new UserAlreadyExistsException(Map.of("email", email));
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("[UserService] Create failed - username already exists: {}", username);
      throw new UserAlreadyExistsException(Map.of("username", username));
    }

    BinaryContent nullableProfile =
        optionalProfileCreateRequest
            .map(
                profileRequest -> {
                  String fileName = profileRequest.fileName();
                  String contentType = profileRequest.contentType();
                  byte[] bytes = profileRequest.bytes();
                  BinaryContent binaryContent =
                      new BinaryContent(fileName, (long) bytes.length, contentType);
                  binaryContentRepository.save(binaryContent);
                  binaryContentStorage.put(binaryContent.getId(), bytes);
                  return binaryContent;
                })
            .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);

    userRepository.save(user);
    log.info("[UserService] Create completed - userId: {}", user.getId());
    return userMapper.toDto(user);
  }

  @Override
  public UserDto find(UUID userId) {
    return userRepository
        .findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAllWithProfileAndStatus().stream().map(userMapper::toDto).toList();
  }

  @Transactional
  @Override
  public UserDto update(
      UUID userId,
      UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.info("[UserService] Update started - userId: {}", userId);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  log.warn("[UserService] Update failed - user not found: {}", userId);
                  return new UserNotFoundException(Map.of("userId", userId));
                });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (newEmail == null || newEmail.isEmpty()) {
      log.warn("[UserService] Update failed - email is null or empty");
      throw new InvalidUserArgumentException(Map.of("email", newEmail != null ? newEmail : ""));
    }
    if (newUsername == null || newUsername.isEmpty()) {
      log.warn("[UserService] Update failed - username is null or empty");
      throw new InvalidUserArgumentException(
          Map.of("username", newUsername != null ? newUsername : ""));
    }
    if (userRepository.existsByEmail(newEmail)) {
      log.warn("[UserService] Update failed - email already exists: {}", newEmail);
      throw new UserAlreadyExistsException(Map.of("email", newEmail));
    }
    if (userRepository.existsByUsername(newUsername)) {
      log.warn("[UserService] Update failed - username already exists: {}", newUsername);
      throw new UserAlreadyExistsException(Map.of("username", newUsername));
    }

    BinaryContent nullableProfile =
        optionalProfileCreateRequest
            .map(
                profileRequest -> {
                  String fileName = profileRequest.fileName();
                  String contentType = profileRequest.contentType();
                  byte[] bytes = profileRequest.bytes();
                  BinaryContent binaryContent =
                      new BinaryContent(fileName, (long) bytes.length, contentType);
                  binaryContentRepository.save(binaryContent);
                  binaryContentStorage.put(binaryContent.getId(), bytes);
                  return binaryContent;
                })
            .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfile);
    log.info("[UserService] Update completed - userId: {}", userId);
    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    log.info("[UserService] Delete started - userId: {}", userId);
    if (!userRepository.existsById(userId)) {
      log.warn("[UserService] Delete failed - user not found: {}", userId);
      throw new UserNotFoundException(Map.of("userId", userId));
    }

    userRepository.deleteById(userId);
    log.info("[UserService] Delete completed - userId: {}", userId);
  }
}
