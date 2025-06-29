package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User UserCreateDtoToUser(UserCreateDto dto) {
        return new User(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword()
        );
    }

    public UserResponseDto UserToUserResponseDto(User user, boolean isOnline, BinaryContent content) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                isOnline,
                content != null,
                content == null ? null : content.getContent()
        );
    }

}
