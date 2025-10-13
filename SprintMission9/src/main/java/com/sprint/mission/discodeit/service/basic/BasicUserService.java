package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;
  private final BasicAuthService authService; // BasicAuthService 주입

  @Transactional
  @Override
  public UserDto create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 생성 시작: {}", userCreateRequest);

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw UserAlreadyExistsException.withEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      throw UserAlreadyExistsException.withUsername(username);
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

    String encodedPassword = passwordEncoder.encode(userCreateRequest.password());
    User user = new User(username, email, encodedPassword, nullableProfile);
    userRepository.save(user);

    UserDto dto = userMapper.toDto(user);
    boolean online = authService.isUserOnline(user.getUsername()); // BasicAuthService 이용
    return new UserDto(dto.id(), dto.username(), dto.email(), dto.profile(), online, dto.role());
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.withId(userId));

    UserDto dto = userMapper.toDto(user);
    boolean online = authService.isUserOnline(user.getUsername());
    return new UserDto(dto.id(), dto.username(), dto.email(), dto.profile(), online, dto.role());
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    return userRepository.findAllWithProfile().stream()
        .map(
            user -> {
              UserDto dto = userMapper.toDto(user);
              boolean online = authService.isUserOnline(user.getUsername());
              return new UserDto(
                  dto.id(), dto.username(), dto.email(), dto.profile(), online, dto.role());
            })
        .toList();
  }

  @PreAuthorize("#userId == authentication.principal.userId")
  @Transactional
  @Override
  public UserDto update(
      UUID userId,
      UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user =
        userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.withId(userId));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByEmail(newEmail)) {
      throw UserAlreadyExistsException.withEmail(newEmail);
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw UserAlreadyExistsException.withUsername(newUsername);
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

    UserDto dto = userMapper.toDto(user);
    boolean online = authService.isUserOnline(user.getUsername());
    return new UserDto(dto.id(), dto.username(), dto.email(), dto.profile(), online, dto.role());
  }

  @PreAuthorize("#userId == authentication.principal.userId")
  @Transactional
  @Override
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료: id={}", userId);
  }
}
