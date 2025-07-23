package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(type = "object", description = "Message 읽음 상태 생성 정보")
public record ReadStatusCreateDto(
        UUID channelId,
        UUID userId
) {
}
