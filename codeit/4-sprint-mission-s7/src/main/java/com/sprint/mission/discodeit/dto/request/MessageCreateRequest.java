package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "메시지 내용은 비어 있을 수 없습니다")
    @Size(max = 5000, message = "메시지 내용은 최대 5000자까지 허용됩니다")
    String content,

    @NotNull(message = "channelId는 null일 수 없습니다")
    UUID channelId,

    @NotNull(message = "authorId는 null일 수 없습니다")
    UUID authorId
) {

}
