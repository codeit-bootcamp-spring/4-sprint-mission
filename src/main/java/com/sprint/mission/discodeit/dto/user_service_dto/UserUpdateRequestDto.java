package com.sprint.mission.discodeit.dto.user_service_dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateRequestDto {
    private final UUID userId;

    private final String newUserName;
    private final String newPassword;
    private final String newProfileImagePath;
    private final String newEmail;

    public UserUpdateRequestDto(UUID userId, String newUserName, String newPassword, String newEmail, String newProfileImagePath) {
        this.userId = userId;

        this.newUserName = newUserName;
        this.newPassword = newPassword;
        this.newEmail = newEmail;
        this.newProfileImagePath = newProfileImagePath;
    }

    // Getters & Setters
}
