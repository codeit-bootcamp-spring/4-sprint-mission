package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserStatusResponseDto {
    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
    private String onlineStatus;
}
