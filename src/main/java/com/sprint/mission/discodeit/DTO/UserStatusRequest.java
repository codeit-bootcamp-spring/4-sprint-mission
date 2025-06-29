package com.sprint.mission.discodeit.DTO;

import java.time.Instant;
import java.util.UUID;

public record UserStatusRequest(
        UUID id,
        UUID userId,
        Instant lastSeenAt
) {}
