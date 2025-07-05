package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.validator.NoBlankIfPresent;
import com.sprint.mission.discodeit.validator.RequiredMultipartFileIfPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

public class UserDto {

    @Getter
    @AllArgsConstructor
    public static class create {
        @NotBlank
        private String username;
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        @RequiredMultipartFileIfPresent
        private MultipartFile image;

        public User toUser(UUID profileId) {
            return new User(
                    username,
                    email,
                    password,
                    profileId
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class update {
        @NoBlankIfPresent
        private String newUsername;
        @NoBlankIfPresent
        private String newEmail;
        @NoBlankIfPresent
        private String newPassword;
        @RequiredMultipartFileIfPresent
        private MultipartFile newProfile;
    }

    public record response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            UUID profileId,
            Boolean online
    ) {
    }
}
