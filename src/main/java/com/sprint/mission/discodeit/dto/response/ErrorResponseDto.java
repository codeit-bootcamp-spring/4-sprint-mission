package com.sprint.mission.discodeit.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        LocalDateTime time,
        int status,
        String error,
        String message
) {
}
