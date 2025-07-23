package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "유저 상태 정보")
public record UserStatusResponseDto(
        @Schema(description = "유저 상태 정보 ID", format = "uuid")
        UUID id,
        @Schema(description = "유저 ID", format = "uuid")
        UUID userId,
        @Schema(description = "최근 활동 시각", format = "date-time")
        Instant lastActiveAt
) {
}
