package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(type = "object", description = "Message 생성 정보")
public record MessageCreateRequest(
        @Schema(description = "메시지 작성 유저 ID", format = "uuid")
        @NotNull(message = "잘못된 ID 입니다.")
        UUID authorId,
        @Schema(description = "메시지가 작성된 채널 ID", format = "uuid")
        @NotNull(message = "잘못된 ID 입니다.")
        UUID channelId,
        @Schema(description = "메시지 내용(본문)", example = "Hello, World!")
        String content
) {
}
