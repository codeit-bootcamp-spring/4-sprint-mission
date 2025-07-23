package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "반환 채널 정보")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChannelResponseDto(
        @Schema(description = "채널 ID", format = "uuid")
        UUID id,
        @Schema(description = "채널 이름", example = "Notification Channel")
        String channelName,
        @Schema(description = "채널 정보", example = "공지 채널입니다.")
        String description,
        @Schema(description = "채널 타입", example = "PUBLIC")
        ChannelType channelType,
        @Schema(description = "채널에 참가중인 유저 ID", example = "[\"uuid-1\",\"uuid-2\"]")
        List<UUID> joinedUserIds,
        @Schema(description = "마지막 메시지 시각", format = "date-time")
        Instant lastMessageTime
) {
}
