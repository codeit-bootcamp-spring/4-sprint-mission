package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto.*;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.UserStatus.UserState;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserStatusMapper {

  // Request
  public UserStatus toUserStatus(UserStatusRequest userStatusRequest) {
    return new UserStatus(userStatusRequest.userId());
  }

  // Response
  public UserStatusResponse toUserStatusResponseDto(UserStatus userStatus) {
    return new UserStatusResponse(
        userStatus.getId(),
        userStatus.getUserId(),
        userStatus.getLastActiveTime(),
        userStatus.getStatus()
    );
  }

  public UserStatusUpdateResponse toUserStatusUpdateResponse(UserStatus userStatus) {
    return new UserStatusUpdateResponse(
        userStatus.getCreatedAt(),
        userStatus.getId(),
        userStatus.getLastActiveTime(),
        userStatus.getStatus() == UserState.ONLINE,
        userStatus.getUpdatedAt(),
        userStatus.getUserId()
    );
  }

  // Response
  public UserStatusResponses toUserStatusResponseDtos(List<UserStatus> userStatusResponseDtos) {
    return new UserStatusResponses(
        userStatusResponseDtos.stream()
            .map(this::toUserStatusResponseDto)
            .toList()
    );
  }

}
