package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto.*;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserStatusMapper {

    // Request
    public UserStatus toUserStatus(UserStatusRequest userStatusRequest) {
        return new UserStatus(userStatusRequest.userId());
    }

    // Response
    public UserStatusResponse toUserStatusResponse(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastActiveAt(),
                userStatus.getUserState()
        );
    }

    public UserStatusUpdateResponse toUserStatusUpdateResponse(UserStatus userStatus) {
        return new UserStatusUpdateResponse(
                userStatus.getCreatedAt(),
                userStatus.getId(),
                userStatus.getLastActiveAt(),
                userStatus.isOnline(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId()
        );
    }

    // Response
    public UserStatusResponses toUserStatusResponses(List<UserStatus> userStatusResponseDtos) {
        return new UserStatusResponses(
                userStatusResponseDtos.stream()
                        .map(this::toUserStatusResponse)
                        .toList()
        );
    }
}
