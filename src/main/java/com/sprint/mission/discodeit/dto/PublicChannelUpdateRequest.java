package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(
        @Schema(description = "새로운 채널 이름", example = "Information Channel")
        String newName,
        @Schema(description = "새로운 채널 정보", example = "정보 채널입니다.")
        String newDescription
) {
}
