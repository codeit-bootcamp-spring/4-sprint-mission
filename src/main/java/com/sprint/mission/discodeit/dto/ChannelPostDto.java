package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(type = "object", description = "Public Channel 생성 정보")
public record ChannelPostDto(
        UUID hostUserId,
        String channelName,
        String description
) {
}
