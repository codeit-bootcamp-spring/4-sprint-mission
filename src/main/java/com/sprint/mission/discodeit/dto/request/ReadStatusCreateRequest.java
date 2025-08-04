package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(type = "object", description = "Message 읽음 상태 생성 정보")
public record ReadStatusCreateRequest(
        @Schema(description = "채널 ID", format = "uuid")
        UUID channelId,
        @Schema(description = "유저 ID", format = "uuid")
        UUID userId,
        @Schema(description = "메세지를 읽은 최근 시간", format = "date-time")
        Instant lastReadAt
) {
}
