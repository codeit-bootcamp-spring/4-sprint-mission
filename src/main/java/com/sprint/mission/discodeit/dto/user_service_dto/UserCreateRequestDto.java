package com.sprint.mission.discodeit.dto.user_service_dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UserCreateRequestDto {
    private final String userName;
    private final String password;
    private final String email;
    private final MultipartFile profileImage;

    // 생성자, getter/setter, builder 등 추가
    public UserCreateRequestDto(String userName, String password, String email, MultipartFile profileImage) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.profileImage = profileImage;

    }
    // Getter 생략
}
