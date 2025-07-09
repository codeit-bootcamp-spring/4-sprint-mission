package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {
    public UserStatus userStatusCreateDtoToUserStatus(UserStatusCreateDto dto) {
        return new UserStatus(
                dto.getUserId()
        );
    }

    public UserStatusResponseDto userStatusToUserStatusResponseDto(UserStatus userStatus) {
        return new UserStatusResponseDto(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getLastAccessedAt(),
                userStatus.isOnline()
        );
    }
}
