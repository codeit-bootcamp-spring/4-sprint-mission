package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusResponseDto createUserStatus(UUID userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException(
            "User with id " + userId + " not found"));

    boolean exists = userStatusRepository.findByUserId(userId).isPresent();
    if (exists) {
      throw new IllegalArgumentException(
          "UserStatus with userId " + userId + " already exists");
    }

    UserStatusEntity userStatusEntity = new UserStatusEntity(userId);
    UserStatusEntity saved = userStatusRepository.save(userStatusEntity);
    return userStatusMapper.UserStatusToUserStatusResponseDto(saved);
  }

  @Override
  public UserStatusResponseDto findUserStatusById(UUID id) {
    UserStatusEntity userStatusEntity = userStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("UserStatus with id " + id + " not found"));
    return userStatusMapper.UserStatusToUserStatusResponseDto(userStatusEntity);
  }

  @Override
  public List<UserStatusResponseDto> findAllUserStatus() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::UserStatusToUserStatusResponseDto)
        .collect(Collectors.toList());
  }

  @Override
  public UserStatusResponseDto updateUserStatus(UserStatusUpdateDto userStatusUpdateDto) {
    UserStatusEntity userStatusEntity = userStatusRepository.findById(userStatusUpdateDto.id())
        .orElseThrow(() -> new IllegalArgumentException(
            "UserStatus with id " + userStatusUpdateDto.id() + " not found"));

    if (userStatusUpdateDto.newLastActiveAt() != null) {
      userStatusEntity.setLastActiveAt(userStatusUpdateDto.newLastActiveAt());
    } else {
      userStatusEntity.updateAccessTime();
    }

    UserStatusEntity updated = userStatusRepository.save(userStatusEntity);
    return userStatusMapper.UserStatusToUserStatusResponseDto(updated);
  }

  @Override
  public UserStatusResponseDto updateUserStatusByUserId(UUID userId,
      UserStatusUpdateDto userStatusUpdateDto) {
    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

    UserStatusEntity userStatusEntity = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new IllegalArgumentException("UserStatus with userId " + userId + " not found"));

    if (userStatusUpdateDto.newLastActiveAt() != null) {
      userStatusEntity.setLastActiveAt(userStatusUpdateDto.newLastActiveAt());
    } else {
      userStatusEntity.updateAccessTime();
    }

    UserStatusEntity updated = userStatusRepository.save(userStatusEntity);
    return userStatusMapper.UserStatusToUserStatusResponseDto(updated);
  }

  @Override
  public void deleteUserStatusById(UUID id) {
    userStatusRepository.findById(id).ifPresentOrElse(
        userStatus -> userStatusRepository.delete(userStatus.getId()),
        () -> {
          throw new IllegalArgumentException("UserStatus with id " + id + " not found");
        }
    );
  }

}
