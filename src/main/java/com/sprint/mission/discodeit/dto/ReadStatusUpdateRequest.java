package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "수정할 읽음 상태 정보")
public record ReadStatusUpdateRequest(
        @Schema(description = "새로운(최근) 읽음 상태 시각", format = "date-time")
        Instant newLastReadAt
) {
}
