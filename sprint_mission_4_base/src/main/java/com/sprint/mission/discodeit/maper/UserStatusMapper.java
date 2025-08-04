package com.sprint.mission.discodeit.maper;


import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class UserStatusMapper {

//    public UserStatus UserStatusCreateDtoToUserStatus(UserStatusCreateDto dto) {
//        return new UserStatus(dto.getUserId());
//    }

    public UserStatusResponseDto UserStatusToUserStatusResponseDto(UserStatus userStatus) {
        return new UserStatusResponseDto(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId(),
                userStatus.getLastActiveAt()
        );
    }

    public UserStatus userIdToUserStatus(UUID id, Instant now) {
        return new UserStatus(id, now);
    }
}
