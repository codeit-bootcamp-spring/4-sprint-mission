package com.sprint.mission.discodeit.dto.user_status_dto;

import com.sprint.mission.discodeit.dto.user_service_dto.UserResponseDto;

public class UserWithStatusResponseDto {
    private UserResponseDto user;
    private String status;

    public UserWithStatusResponseDto(UserResponseDto user, String status) {
        this.user = user;
        this.status = status;
    }

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
