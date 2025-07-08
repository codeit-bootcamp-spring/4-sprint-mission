package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record MessageUpdateDto (
        UUID id,
        String newMessage
) {}
