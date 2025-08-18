package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record UserStatusUpdateRequest(
    @NotNull(message = "newLastActiveAt은 null일 수 없습니다")
    @PastOrPresent(message = "newLastActiveAt은 현재 시각을 초과할 수 없습니다")
    Instant newLastActiveAt
) {

}
