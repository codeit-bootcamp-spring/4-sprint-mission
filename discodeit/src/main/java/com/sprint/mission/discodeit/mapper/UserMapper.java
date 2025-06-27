package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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

    public UserResponseDto userToUserResponseDto(User user, UserStatus status) {
        UserStatusDto statusDto = new UserStatusDto(
                status != null ? status.isOnline() : "offline",
                status != null ? status.getLastActiveAt() : null
        );

        UserResponseDto dto = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                statusDto
        );
        return dto;
    }

    public BinaryContent binaryContentDtoToEntity(BinaryContentDto dto) {
        BinaryContent binaryContent = new BinaryContent(
                dto.getUserId(),
                dto.getMessageId(),
                dto.getData(),
                dto.getFileName(),
                dto.getFileType()
        );

        return binaryContent;
    }

    public LoginResponseDto toLoginResponseDto(User user, BinaryContent profile) {
        BinaryContentDto dto = null;
        if (profile != null) {
            dto = new BinaryContentDto(
                    profile.getUserId(),
                    profile.getMessageId(),
                    profile.getDatas(),
                    profile.getFilename(),
                    profile.getFileType()
            );
        }

        return new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                dto,
                user.getLastLoginAt()
        );
    }

}
