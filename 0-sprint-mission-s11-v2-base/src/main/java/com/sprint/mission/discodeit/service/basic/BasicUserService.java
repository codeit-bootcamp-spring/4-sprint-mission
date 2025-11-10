package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.config.CacheConfig.USERS_ALL;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.event.S3UploadEvent;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.sse.SseRepository;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
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
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;
  private final SseService sseService;
  private final SseRepository sseRepository;

  private static final String SSE_USER_CREATED_EVENT_NAME = "users.created";
  private static final String SSE_USER_UPDATED_EVENT_NAME = "users.updated";
  private static final String SSE_USER_DELETED_EVENT_NAME = "users.deleted";

  //id	이벤트 고유 ID
  //name	users.created or updated or deleted
  //data	UserDto

  @CacheEvict(cacheNames = USERS_ALL, key = "'USER_ALL'")
  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
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

    String password = userCreateRequest.password();
    String encodedPassword = passwordEncoder.encode(password);

    User user = new User(username, email, encodedPassword, null);
    user = userRepository.save(user);
    UUID userId = user.getId();

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          eventPublisher.publishEvent(new S3UploadEvent(userId, binaryContent.getId(), bytes));
          //binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);

    user.setProfile(nullableProfile);

    userRepository.save(user);
    log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);

    UserDto userDto = userMapper.toDto(user);
    sseService.send(sseRepository.getAllUserId(), SSE_USER_CREATED_EVENT_NAME, userDto);
    log.info("유저 생성 후 SSE 발송");

    return userDto;
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: id={}", userId);
    UserDto userDto = userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    log.info("사용자 조회 완료: id={}", userId);
    return userDto;
  }

  @Cacheable(cacheNames = USERS_ALL, key = "'USER_ALL'",  sync = true)
  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
    List<UserDto> userDtos = userRepository.findAllWithProfile()
        .stream()
        .map(userMapper::toDto)
        .toList();
    log.info("모든 사용자 조회 완료: 총 {}명", userDtos.size());
    return userDtos;
  }

  @CacheEvict(cacheNames = USERS_ALL, key = "'USER_ALL'")
  @PreAuthorize("principal.userDto.id == #userId")
  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          UserNotFoundException exception = UserNotFoundException.withId(userId);
          return exception;
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByEmail(newEmail)) {
      throw UserAlreadyExistsException.withEmail(newEmail);
    }

    if (userRepository.existsByUsername(newUsername)) {
      throw UserAlreadyExistsException.withUsername(newUsername);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          eventPublisher.publishEvent(new S3UploadEvent(userId, binaryContent.getId(), bytes));
          //binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    String encodedPassword = Optional.ofNullable(newPassword).map(passwordEncoder::encode)
        .orElse(user.getPassword());
    user.update(newUsername, newEmail, encodedPassword, nullableProfile);

    log.info("사용자 수정 완료: id={}", userId);

    UserDto userDto = userMapper.toDto(user);
    sseService.send(sseRepository.getAllUserId(), SSE_USER_UPDATED_EVENT_NAME, userDto);
    log.info("유저 수정 후 SSE 발송");

    return userDto;
  }

  @CacheEvict(cacheNames = USERS_ALL, key = "'USER_ALL'")
  @PreAuthorize("principal.userDto.id == #userId")
  @Transactional
  @Override
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    UserDto userDto = userMapper.toDto(user);
    userRepository.delete(user);

    sseService.send(sseRepository.getAllUserId(), SSE_USER_DELETED_EVENT_NAME, userDto);
    log.info("유저 삭제 후 SSE 발송");


    log.info("사용자 삭제 완료: id={}", userId);
  }

  @CacheEvict(cacheNames = USERS_ALL, key = "'USER_ALL'")
  @Transactional
  @Override
  public void updateUserRoles(UUID userId, Role role) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.updateRole(role);
    User saved = userRepository.save(user);
    log.info("유저 역할 변경및 저장: id={}, 새 역할={}", saved.getId(), saved.getRole());
  }
}
