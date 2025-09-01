package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotNull(message = "newLastReadAt은 null일 수 없습니다")
    @PastOrPresent(message = "newLastReadAt은 현재 시각을 초과할 수 없습니다")
    Instant newLastReadAt
) {

}
