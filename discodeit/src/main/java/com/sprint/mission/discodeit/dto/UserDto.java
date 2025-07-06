package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentCreateDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class UserDto {

    @AllArgsConstructor
    @Getter
    public static class UserCreateDto {
        private String username;
        private String email;
        private String password;
        private UUID contentId;
        private MultipartFile profile;
    }

    @AllArgsConstructor
    @Getter
    public static class UserResponseDto {
        private UUID id;
        private String username;
        private String email;
        private UUID profileId;
        private UserStatusDto userStatusDto;
    }

    @AllArgsConstructor
    @Getter
    public static class UserUpdateDto {
        private UUID userId;
        private String username;
        private String email;
        private String password;
        private BinaryContentCreateDto newProfile;
    }
}
