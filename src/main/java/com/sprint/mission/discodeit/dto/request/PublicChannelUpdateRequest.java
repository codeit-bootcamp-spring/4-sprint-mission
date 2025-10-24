package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(
        @Schema(description = "새로운 채널 이름", example = "Information Channel")
        @NotBlank(message = "변경할 채널이름이 올바르지 않습니다.")
        String newName,
        @Schema(description = "새로운 채널 정보", example = "정보 채널입니다.")
        @NotBlank(message = "변경할 채널 정보가 올바르지 않습니다.")
        String newDescription
) {
}
