package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @Schema(description = "마지막 활동 시간 (사용자가 온라인 상태임을 나타냄")
    Instant newLastActiveAt
) {

}
