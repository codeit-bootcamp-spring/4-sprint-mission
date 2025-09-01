package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(

    @NotNull(message = "userId는 null일 수 없습니다")
    UUID userId,

    @NotNull(message = "lastActiveAt은 null일 수 없습니다")
    @PastOrPresent(message = "lastActiveAt은 현재 시각을 초과할 수 없습니다")
    Instant lastActiveAt
) {

}
