package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User userCreateDtoToUser(UserCreateDto dto) {
        User user = new User(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword()
        );
        return user;
    }

    public UserResponseDto userToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                user.getUserStatus().isOnline(),
                user.getUserStatus()
        );
    }
}
