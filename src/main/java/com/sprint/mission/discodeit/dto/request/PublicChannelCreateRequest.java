package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(
        @Schema(description = "채널 이름", example = "Notification Channel")
        @NotBlank(message = "채널 이름은 필수입니다.")
        String name,
        @Schema(description = "채널 정보", example = "공지 채널입니다.")
        @NotBlank(message = "채널 정보는 필수입니다.")
        String description
) {
}
