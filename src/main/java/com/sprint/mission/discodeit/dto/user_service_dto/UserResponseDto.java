package com.sprint.mission.discodeit.dto.user_service_dto;

import lombok.Getter;


import java.util.UUID;

@Getter
public class UserResponseDto {
    private UUID userId;
    private String userName;
    private String userEmail;
    private byte[] userPicture;


    public UserResponseDto(UUID userId, String userName, String userEmail, byte[] userPicture) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPicture = userPicture;
    }
}
