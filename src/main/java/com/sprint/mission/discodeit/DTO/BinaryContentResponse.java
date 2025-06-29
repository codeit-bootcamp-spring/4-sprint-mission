package com.sprint.mission.discodeit.DTO;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        Instant createdAt,
        byte[] content
) {}
