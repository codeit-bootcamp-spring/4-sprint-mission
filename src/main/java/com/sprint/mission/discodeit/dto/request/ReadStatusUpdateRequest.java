package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

@Schema(description = "수정할 읽음 상태 정보")
public record ReadStatusUpdateRequest(
        @Schema(description = "새로운(최근) 읽음 상태 시각", format = "date-time")
        @PastOrPresent(message = "최근 읽음 상태는 과거이거나 현재여야 합니다.")
        Instant newLastReadAt
) {
}
