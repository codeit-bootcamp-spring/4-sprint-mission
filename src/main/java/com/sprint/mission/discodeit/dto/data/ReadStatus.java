package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record ReadStatus(
        @Schema(description = "ReadStatus UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "ReadStatus 생성 시간", example = "2025-07-10T02:15:30.123Z")
        Instant createdAt,
        @Schema(description = "최근 ReadStatus 업데이트 시간", example = "2025-07-10T02:15:30.123Z")
        Instant updatedAt,
        @Schema(description = "유저 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID userId,
        @Schema(description = "채널 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID channelId,
        @Schema(description = "마지막 열람 시간", example = "2025-07-10T02:15:30.123Z")
        Instant lastReadAt
) {
}
