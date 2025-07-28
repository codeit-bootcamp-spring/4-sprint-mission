package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto find(UUID userStatusId) {
    return userStatusMapper.mapToDto(getFindingStatus(userStatusId));
  }

  // PAGING !!!!!!!!!!!!!!!
  @Override
  @Transactional(readOnly = true)
  public List<UserStatusDto> findAll() {
    return userStatusMapper.mapToDtoList(userStatusRepository.findAll());
  }

  @Override
  @Transactional
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus userStatus = getFindingStatus(userStatusId);

    UserStatus updatedUserStatus = userStatusMapper.mapToUpdateEntity(request);
    if (updatedUserStatus.getLastActiveAt() != null) {
      userStatus.setLastActiveAt(updatedUserStatus.getLastActiveAt());
    }

    return userStatusMapper.mapToDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    User user = getFindingUser(userId);
    UserStatus userStatus = user.getStatus();
    userStatusMapper.mapToEntity(user, request.newLastActiveAt());
    if (request.newLastActiveAt() != null) {
      userStatus.setLastActiveAt(request.newLastActiveAt());
    }

    return userStatusMapper.mapToDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public void delete(UUID userStatusId) {
    userStatusRepository.delete(getFindingStatus(userStatusId));
  }

  // 공통 메서드
  private UserStatus getFindingStatus(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
  }

  private User getFindingUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }

}
