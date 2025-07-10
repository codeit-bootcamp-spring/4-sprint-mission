package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.UserDto.*;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public UserGetDto create(UserCreateRequest userCreateRequest, MultipartFile profileRequest) {
    if (existsByUsernameOrEmail(userCreateRequest.username(),
        userCreateRequest.email())) {
      throw new IllegalStateException("User with the same username or email already exists.");
    }

    //Save profile in binaryContentRepository
    UUID nullableProfileId = null;
    if (profileRequest != null && !profileRequest.isEmpty()) {
      try {
        String fileName = profileRequest.getOriginalFilename();
        String contentType = profileRequest.getContentType();
        byte[] bytes = profileRequest.getBytes();
        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
            contentType, bytes);
        nullableProfileId = binaryContentRepository.save(binaryContent).getId();
      } catch (IOException e) {
        // Handle file reading exception
        throw new RuntimeException("Failed to process profile picture", e);
      }
    }

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();
    String password = userCreateRequest.password();

    User createdUser = userRepository.save(new User(username, email, password, nullableProfileId));
    UserStatus userStatus = new UserStatus(createdUser.getId());
    userStatusRepository.save(userStatus);

    return userMapper.toUserGetDto(createdUser, userStatus);
  }

  @Override
  public UserResponse find(UUID userId) {

    // find the userStatus
    Optional<UserStatus> userStatus = userStatusRepository.findByUserId(userId);

    return userRepository.findById(userId)
        .map(u -> userMapper.toUserResponseDto(u, userStatus))
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }


  // 심화 요구사항에 맞춰서 findAll 변경
  @Override
  public List<AllUserGetDto> findAll() {
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      return Collections.emptyList();
    }

    return users.stream()
        .map(u -> {
          UserStatus userStatus = userStatusRepository.findByUserId(u.getId())
              .orElseThrow(() -> new NoSuchElementException(
                  "UserStatus does not exist for user id " + u.getId()));
          return userMapper.toAllUserGetDto(u, userStatus);
        })
        .toList();
  }

  @Override
  public UserUpdateResponse update(UUID userId, UserUpdateRequest userUpdateRequest,
      MultipartFile profileRequest) {

    User existingUser = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    if (userUpdateRequest.newUsername() != null) {
      existingUser.setUsername(userUpdateRequest.newUsername());
    }
    if (userUpdateRequest.newEmail() != null) {
      existingUser.setEmail(userUpdateRequest.newEmail());
    }
    if (userUpdateRequest.newPassword() != null) {
      existingUser.setPassword(userUpdateRequest.newPassword());
    }

    // new profile if any
    //Save profile in binaryContentRepository
    UUID nullableProfileId = null;
    if (profileRequest != null && !profileRequest.isEmpty()) {
      try {
        String fileName = profileRequest.getOriginalFilename();
        String contentType = profileRequest.getContentType();
        byte[] bytes = profileRequest.getBytes();
        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
            contentType, bytes);
        nullableProfileId = binaryContentRepository.save(binaryContent).getId();
      } catch (IOException e) {
        // Handle file reading exception
        throw new RuntimeException("Failed to process profile picture", e);
      }
      existingUser.setProfileId(nullableProfileId);
    }
    User updatedUser = userRepository.save(existingUser);

    return userMapper.toUpdateUserResponseDto(updatedUser);
  }

  @Override
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    // deleting in userstatus
    UserStatus userStatus = userStatusRepository.findByUserId(id)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus for user id " + id + " does not exist!"));
    userStatusRepository.deleteById(userStatus.getId());

    // deleting in binarycontent
    Optional.ofNullable(user.getProfileId())
        .ifPresent(binaryContentRepository::deleteById);

    // deleting user
    userRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    userRepository.deleteAll();
  }

  private boolean existsByUsernameOrEmail(String username, String email) {
    List<User> users = userRepository.findAll();

    List<User> filtered = users.stream().
        filter(
            u -> Objects.equals(u.getUsername(), username) || Objects.equals(u.getEmail(), email))
        .toList();

    return !filtered.isEmpty();
  }

}
