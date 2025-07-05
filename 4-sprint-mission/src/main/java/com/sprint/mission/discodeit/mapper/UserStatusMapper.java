package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserStatusMapper {
    public UserStatus UserStatusCreateDtoToUserStatus(UserStatusCreateDto userStatusCreateDto) {
        return new UserStatus(userStatusCreateDto.userId());
    }

    public UserStatusResponseDto UserStatusToUserStatusResponseDto(UserStatus userStatus) {
        return new UserStatusResponseDto(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.isOnline(),
                userStatus.getLastAccessAt());
    }
}
