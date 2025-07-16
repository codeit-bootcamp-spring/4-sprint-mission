package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;
import java.util.UUID;

public record Channel(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String type,
    String name,
    String description
) {

}
