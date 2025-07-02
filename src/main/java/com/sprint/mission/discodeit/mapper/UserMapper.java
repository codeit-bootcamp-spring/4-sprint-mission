package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserMapper() {

    }

    public User toEntity(UserCreateDto dto) {

        return new User(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword(),
                null
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
