package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.PresenceStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    public User ofUser(UserCreateRequest userCreateRequest, UUID profileId) {
        return new User(userCreateRequest.username(), userCreateRequest.password(), userCreateRequest.email(), profileId);
    }

    public UserResponseDto ofUserResponseDto(User user, UserStatusResponseDto userStatusResponseDto) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getProfileId(), userStatusResponseDto);
    }

    public UserLoginResponseDto toUserLoginResponseDto(User user) {
        return new UserLoginResponseDto(user.getId(), user.getProfileId(), user.getUsername(), user.getEmail(), user.getPresenceStatus());
    }

    public UserCreateResponseDto ofUserCreateResponseDto(User user, UserStatusResponseDto userStatusResponseDto) {
        return new UserCreateResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getProfileId(), userStatusResponseDto);
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getProfileId(), user.getPresenceStatus().equals(PresenceStatus.ONLINE));
    }
}
