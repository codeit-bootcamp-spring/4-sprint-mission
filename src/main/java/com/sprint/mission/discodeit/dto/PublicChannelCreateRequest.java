package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(
        @Schema(description = "채널 이름", example = "Notification Channel")
        String name,
        @Schema(description = "채널 정보", example = "공지 채널입니다.")
        String description
) {
}
