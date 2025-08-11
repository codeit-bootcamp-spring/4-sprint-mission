package com.codeit.discodeit.dto.user_status_dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusResponseDto {
    private final UUID userStatusId;
    private final UUID userId;
    private final String userName;
    private final Instant loginTime;

    public UserStatusResponseDto(UUID userStatusId, UUID userId, String userName, Instant loginTime) {
        this.userStatusId = userStatusId;
        this.userId = userId;
        this.userName = userName;
        this.loginTime = loginTime;
    }
}
