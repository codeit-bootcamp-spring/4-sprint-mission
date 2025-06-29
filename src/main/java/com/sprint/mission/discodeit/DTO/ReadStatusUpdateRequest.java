package com.sprint.mission.discodeit.DTO;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {}
