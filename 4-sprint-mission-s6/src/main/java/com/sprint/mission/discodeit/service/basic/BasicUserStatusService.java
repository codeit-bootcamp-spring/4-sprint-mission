package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " does not exist");
    }
    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
    }

    UserStatus userStatus = userStatusMapper.toEntity(request);

    UserStatus saved = userStatusRepository.save(userStatus);

    return userStatusMapper.toDto(saved);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(
                    () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public List<UserStatusDto> findAll() {
    List<UserStatus> list = userStatusRepository.findAll().stream()
            .toList();

    return list.stream()
            .map(userStatusMapper::toDto)
            .toList();
  }

  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));

    userStatusMapper.updateFromRequest(request, userStatus);

    UserStatus updated = userStatusRepository.save(userStatus);

    return userStatusMapper.toDto(updated);
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {

    UserStatus userStatus = userStatusRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userId + " not found"));

    userStatusMapper.updateFromRequest(request, userStatus);

    UserStatus updated = userStatusRepository.save(userStatus);

    return userStatusMapper.toDto(updated);
  }

  @Override
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }
    userStatusRepository.deleteById(userStatusId);
  }
}
