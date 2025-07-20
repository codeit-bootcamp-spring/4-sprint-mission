package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {
    public User userCreateDtoToUser(UserCreateRequest dto, UUID fileId) {
        User user = new User(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword(),
                fileId
                );
        return user;
    }

    public UserResponseDto userToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFileId(),
                user.getUserStatus() != null && user.getUserStatus().isOnline(),
                user.getUserStatus()
        );
    }

    public UserDto userToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getFileId(),
                user.getUserStatus() != null && user.getUserStatus().isOnline()
        );
    }
}
