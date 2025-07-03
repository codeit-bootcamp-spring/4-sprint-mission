package com.sprint.mission.discodeit.dto.user_service_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Base64;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {
    private UUID userId;
    private String userName;
    private String userEmail;
    private byte[] userPicture;
    private String picturePath;


    public UserResponseDto(UUID userId, String userName, String userEmail, byte[] userPicture) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPicture = userPicture;
    }

    public UserResponseDto(UUID userId, String userName, String userEmail, String picturePath) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.picturePath = picturePath.substring(1);
    }

    public String getProfileImageBase64() {
        if (userPicture != null && userPicture.length > 0) {
            return Base64.getEncoder().encodeToString(userPicture);
        }
        return null;
    }
}
