package com.sprint.mission.discodeit.dto.readStsuts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor public class ReadStatusCreateDto {
    @Schema(description = "사용자 ID", example = "2fd4b22a-2bda-4503-9adf-02f803cce082")
    @NotNull
    private final UUID userId;

    @Schema(description = "채널 ID", example = "3d0b4ba7-5b04-4287-9fe7-3a69950ad7c1")
    @NotNull
    private final UUID channelId;

    @Schema(description = "마지막으로 읽은 시간", example = "2025-07-20T15:57:48.774Z")
    private final Instant lastReadAt;
}
