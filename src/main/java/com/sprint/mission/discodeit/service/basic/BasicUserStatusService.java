package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto.*;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusResponse create(UserStatusRequest userStatusRequest) {
    // Throw error if user not found
    if (!userRepository.existsById(userStatusRequest.userId())) {
      throw new IllegalArgumentException("User not found");
    }

    // Throw error if userstatus already exists
    userStatusRepository.findByUserId(userStatusRequest.userId())
        .ifPresent(s -> {
          throw new IllegalArgumentException(
              "User status already exists for user with ID: " + s.getUserId());
        });

    UserStatus newUserStatus = userStatusMapper.toUserStatus(userStatusRequest);
    return userStatusMapper.toUserStatusResponseDto(userStatusRepository.save(newUserStatus));
  }

  @Override
  public UserStatusResponse find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toUserStatusResponseDto)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
  }

  @Override
  public UserStatusResponse findByUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .map(userStatusMapper::toUserStatusResponseDto)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with user id " + userId + " not found"));
  }

  @Override
  public UserStatusResponses findAll() {
    return userStatusMapper.toUserStatusResponseDtos(userStatusRepository.findAll());
  }

  @Override
  public UserStatusUpdateResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toUserStatusUpdateResponse(userStatusRepository.save(userStatus));
  }


  @Override
  public UserStatusUpdateResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toUserStatusUpdateResponse(userStatusRepository.save(userStatus));
  }

  @Override
  public void delete(UUID id) {
    if (!userStatusRepository.existsById(id)) {
      throw new NoSuchElementException("UserStatus with id " + id + " not found");
    }
    userStatusRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    userStatusRepository.deleteAll();
  }
}
