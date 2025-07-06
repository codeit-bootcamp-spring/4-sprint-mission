package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.LoginResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserDto.UserResponseDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentCreateDto;

import java.io.IOException;

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

    public BinaryContent binaryContentDtoToEntity(BinaryContentCreateDto dto) {
        MultipartFile file = dto.getFile();

        byte[] data = null;
        String fileName = null;
        String contentType = null;

        if (file != null && !file.isEmpty()) {
            try {
                data = file.getBytes();
                fileName = file.getOriginalFilename();
                contentType = file.getContentType();
            } catch (IOException e) {
                throw new RuntimeException("파일 변환 중 오류 발생", e);
            }
        }

        return new BinaryContent(
                dto.getUserId(),
                dto.getMessageId(),
                data,
                fileName,
                contentType);
    }

    public LoginResponseDto toLoginResponseDto(User user, BinaryContent profile) {
        BinaryContentResponseDto dto = null;

        if (profile != null) {
            dto = new BinaryContentResponseDto(
                    profile.getId(),
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
