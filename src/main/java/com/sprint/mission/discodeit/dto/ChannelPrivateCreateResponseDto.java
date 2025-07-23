package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(type = "ojbect", description = "Private Channel 생성 정보")
public record ChannelPrivateCreateResponseDto(
        @Schema(description = "채널 ID", format = "uuid")
        UUID id,
        @Schema(description = "채널 타입", example = "PRIVATE")
        ChannelType channelType,
        @Schema(description = "채널 참가 유저들의 ID", example = "[\"uuid-1\",\"uuid-2\"]")
        List<UUID> ids
) {
}
