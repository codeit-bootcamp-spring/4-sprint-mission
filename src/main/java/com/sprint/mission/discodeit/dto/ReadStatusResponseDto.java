package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponseDto(
        @Schema(description = "ReadStatus ID", format = "uuid")
        UUID id,
        @Schema(description = "유저 ID", format = "uuid")
        UUID userId,
        @Schema(description = "채널 ID", format = "uuid")
        UUID channelId,
        @Schema(description = "메시지를 읽은 최근 시간", format = "date-time")
        Instant latestTime
) {
}
