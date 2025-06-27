package com.sprint.mission.discodeit.dto.user_status_dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusResponseDto {
    private final UUID userStatusId;
    private final UUID userId;
    private final String userName;
    private final String isLoggedIn;
    private final Instant loginTime;

    public UserStatusResponseDto(UUID userStatusId, UUID userId, String userName, String loggedIn, Instant loginTime) {
        this.userStatusId = userStatusId;
        this.userId = userId;
        this.userName = userName;
        this.isLoggedIn = loggedIn;
        this.loginTime = loginTime;
    }
}
