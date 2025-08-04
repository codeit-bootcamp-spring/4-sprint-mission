package com.sprint.mission.discodeit.maper;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    public User UserCreateRequestToUser(UserCreateRequest dto, UUID nullableProfileId) {
        return new User(
                dto.username(),
                dto.email(),
                dto.password(),
                nullableProfileId
        );
    }

    public UserResponseDto UserToUserResponseDto(User user, boolean online) {
        return new UserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }

}
