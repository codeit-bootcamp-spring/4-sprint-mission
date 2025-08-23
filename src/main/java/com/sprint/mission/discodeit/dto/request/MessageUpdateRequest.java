package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

public record MessageUpdateRequest(
        @NotNull(message = "본문은 null일 수 없습니다.")
        String newContent
) {

}
