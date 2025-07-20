package com.sprint.mission.discodeit.dto.readStsuts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class ReadStatusUpdateDto {
    @Schema(description = "마지막으로 읽은 시간", example = "2025-07-20T15:57:48.774Z")
    @NotNull
    private final Instant newLastReadAt;
}
