package com.sprint.mission.discodeit.dto.user_service_dto;

import lombok.Getter;

@Getter
public class UserCreateRequestDto {
    private String username;
    private String password;
    private String email;
    private String profileImagePath;

    // 생성자, getter/setter, builder 등 추가
    public UserCreateRequestDto(String username, String password, String email, String profileImagePath) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImagePath = profileImagePath;
    }

    // Getter 생략
}
