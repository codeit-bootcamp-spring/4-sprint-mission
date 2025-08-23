package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreateRequest(
        @NotNull(message = "본문은 null일 수 없습니다.")
        String content,
        UUID channelId,
        UUID authorId
) {

}
