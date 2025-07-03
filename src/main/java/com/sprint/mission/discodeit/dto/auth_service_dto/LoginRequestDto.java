package com.sprint.mission.discodeit.dto.auth_service_dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private final String userName;
    private final String password;

    public LoginRequestDto(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
