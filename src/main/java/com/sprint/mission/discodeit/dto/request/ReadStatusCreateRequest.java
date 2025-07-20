package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @Schema(description = "읽음 상태를 기록할 사용자의 고유 ID")
    UUID userId,

    @Schema(description = "읽음 상태를 기록할 채널의 고유 ID")
    UUID channelId,

    @Schema(description = "메시지를 마지막으로 읽은 시간")
    Instant lastReadAt
) {

}
