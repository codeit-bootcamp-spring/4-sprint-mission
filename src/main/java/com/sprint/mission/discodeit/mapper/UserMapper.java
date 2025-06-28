package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserMapper {
    public User userCreateDtoToUser(UserCreateDto dto) {
        return new User(
                dto.username(),
                dto.email(),
                dto.password()
        );
    }

    public UserResponseDto userToUserResponseDto(User user, UserStatus userStatus) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.getUserStatusType(),
                userStatus.getLastLoginTimes()
        );
    }
}
