package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {

    public UserStatus UserStatusCreateDtoToUserStatus(UserStatusCreateDto dto) {
        return new UserStatus(dto.getUserId());
    }

    public UserStatusResponseDto UserStatusToUserStatusResponseDto(UserStatus userStatus) {
        return new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), userStatus.getLoginType());
    }
}
