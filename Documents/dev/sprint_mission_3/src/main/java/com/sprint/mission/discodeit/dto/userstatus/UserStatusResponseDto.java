package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        Instant lasAccessedAt,
        boolean isOnline
) {}
