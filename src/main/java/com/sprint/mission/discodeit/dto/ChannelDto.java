package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "채널 정보")
public record ChannelDto(
        @Schema(description = "채널 ID", format = "uuid")
        UUID id,
        @Schema(description = "채널 타입", example = "PUBLIC")
        ChannelType type,
        @Schema(description = "채널 이름", example = "Notification Channel")
        String name,
        @Schema(description = "채널 정보", example = "공지 채널입니다.")
        String description,
        @Schema(description = "채널에 참가중인 유저들 ID", example = "[\"uuid-1\",\"uuid-2\"]")
        List<UUID> participantIds,
        @Schema(description = "마지막 메시지 시각", format = "date-time")
        Instant lastMessageAt
) {
}
