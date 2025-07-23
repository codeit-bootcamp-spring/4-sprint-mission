package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusPostDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserStatusMapper {
    public UserStatus toUserStatus(UserStatusPostDto postDto) {
        return new UserStatus(postDto.userId(), postDto.latestActiveAt(), true);
    }

    public UserStatusResponseDto toUserStatusResponseDto(UserStatus userStatus) {
        return new UserStatusResponseDto(userStatus.getId(), userStatus.getUserId(), userStatus.getLastActiveAt());
    }
}
