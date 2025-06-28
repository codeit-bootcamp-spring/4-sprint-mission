package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

    public UserStatus userStatusCreateDtoToUserStatus(UserStatusCreateDto userStatusCreateDto) {
        return new UserStatus(
                userStatusCreateDto.userId(),
                userStatusCreateDto.lastLoginTimes()
        );
    }

    public UserStatusResponseDto userStatusToUserStatusResponseDto(UserStatus userStatus) {
        return new UserStatusResponseDto(
                userStatus.getId(),
                userStatus.getLastLoginTimes(),
                userStatus.getUserStatusType()
        );
    }
}
