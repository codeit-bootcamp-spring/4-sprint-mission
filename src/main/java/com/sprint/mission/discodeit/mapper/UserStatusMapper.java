package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

public class UserStatusMapper {

    public UserStatusMapper() {}

    public UserStatus toEntity(UserStatusCreateDto dto) {

        return new UserStatus(
                dto.getUserId()
        );
    }

    public UserStatusResponseDto toDto(User user, UserStatus userStatus) {

        return new UserStatusResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                userStatus.getUpdatedAt()
        );
    }
}
