package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID messageId,
        String newContent
) {}
