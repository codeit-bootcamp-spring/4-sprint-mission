package com.sprint.mission.discodeit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class UserResponseDto {
    private final UUID id;
    private final String username;
    private final String email;
    private final UUID profileId;
    private final boolean isOnline;
    private final Instant createdAt;
    private final Instant updatedAt;
}
