package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(
    UUID id,
    UUID userId,
    Instant createdAt,
    Instant updatedAt,
    Instant lastActiveAt,
    boolean online
) {

}