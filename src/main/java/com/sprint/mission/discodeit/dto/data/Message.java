package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Message(
        @Schema(description = "메세지 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "메세지 생성 시간", example = "2025-07-10T02:15:30.123Z")
        Instant createdAt,
        @Schema(description = "최근 메세지 업데이트 시간", example = "2025-07-10T02:15:30.123Z")
        Instant updatedAt,
        @Schema(description = "메세지 내용", example = "Hello, nice to meet you")
        String content,
        @Schema(description = "메세지가 작성된 채널 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID channelId,
        @Schema(description = "메세지 작성자 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID authorId,
        @Schema(description = "첨부 파일 UUID 리스트", example = "[\n" +
                "    \"41afbed1-2670-4431-aafe-56d6e6df99c9\",\n" +
                "    \"a7e92c8f-62d9-4d6f-b7f3-2b8be601c4a5\",\n" +
                "    \"f2c4f2c9-8fcb-4b39-b2d7-1e339d776b49\"\n" +
                "  ]")
        List<UUID> attachmentIds
) {
}
