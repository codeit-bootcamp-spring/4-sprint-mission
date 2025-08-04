package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        @Schema(description = "메시지 ID", format = "uuid")
        UUID id,
        @Schema(description = "메시지 작성 유저 ID")
        UserDto author,
        @Schema(description = "메시지가 작성된 채널 ID", format = "uuid")
        UUID channelId,
        @Schema(description = "메시지 내용", example = "Hello, World!")
        String content,
        @Schema(description = "메시지에 포함된 파일 ID들")
        List<BinaryContentDto> attachments,
        @Schema(description = "메시지 최초 작성 시각", format = "date-time")
        Instant createdAt,
        @Schema(description = "메시지 최근 수정 시각", format = "date-time")
        Instant updatedAt
) {
}
