package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(type = "object", description = "공개 채널 응답 객체")
public record ChannelPublicCreateResponseDto(
        @Schema(description = "채널 ID", format = "uuid")
        UUID id,
        @Schema(description = "채널 타입", example = "PUBLIC")
        ChannelType channelType,
        @Schema(description = "채널 이름", example = "Notification Channel")
        String name,
        @Schema(description = "채널 설명", example = "공지 채널입니다.")
        String description
) {
}
