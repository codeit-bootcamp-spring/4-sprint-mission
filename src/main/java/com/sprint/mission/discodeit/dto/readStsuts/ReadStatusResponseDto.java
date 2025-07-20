package com.sprint.mission.discodeit.dto.readStsuts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
public class ReadStatusResponseDto {
    @Schema(description = "읽음 상태(ReadStatus) ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID id;

    @Schema(description = "마지막으로 읽은 시간", example = "2025-07-08T09:55:59.735Z")
    private final Instant lastReadAt;

    @Schema(description = "사용자 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID userId;

    @Schema(description = "채널 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID channelId;

    @Schema(description = "생성일", example = "2025-07-08T09:55:59.735Z")
    private final Instant createdAt;

    @Schema(description = "수정일", example = "2025-07-08T09:55:59.735Z")
    private final Instant updatedAt;
}
