package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusRequestDto(
        UUID userId,
        UUID channelId,
        Instant readAt
) {
    public static ReadStatusRequestDto create (UUID userId, UUID channelId) {
        return new ReadStatusRequestDto(userId, channelId, Instant.now());
    }
}
