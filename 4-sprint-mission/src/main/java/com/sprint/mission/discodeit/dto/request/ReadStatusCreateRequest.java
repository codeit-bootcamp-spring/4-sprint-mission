package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(type = "object", description = "Message 읽음 상태 생성 정보")
public record ReadStatusCreateRequest(
        @Schema(description = "채널 ID", format = "uuid")
        @NotNull(message = "잘못된 ID 입니다.")
        UUID channelId,
        @Schema(description = "유저 ID", format = "uuid")
        @NotNull(message = "잘못된 ID 입니다.")
        UUID userId,
        @Schema(description = "메세지를 읽은 최근 시간", format = "date-time")
        Instant lastReadAt
) {
}
