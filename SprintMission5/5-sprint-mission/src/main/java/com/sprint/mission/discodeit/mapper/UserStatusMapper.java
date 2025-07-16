package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserStatusMapper {

  public UserStatusResponseDto UserStatusToUserStatusResponseDto(
      UserStatusEntity userStatusEntity) {
    return new UserStatusResponseDto(
        userStatusEntity.getId(),
        userStatusEntity.getUserId(),
        userStatusEntity.getCreatedAt(),
        userStatusEntity.getUpdatedAt(),
        userStatusEntity.getLastActiveAt(),
        userStatusEntity.isOnline());
  }
}
