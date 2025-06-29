package com.sprint.mission.discodeit.DTO;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        UUID userId,
        Instant createdAt,
        Instant updatedAt,
        Instant lastSeenAt
) {}
