package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "Private Channel 생성 정보")
public record PrivateChannelCreateRequest(
        @Schema(description = "비공개 채널 참가자들의 ID", format = "uuid", example = "[\"uuid-1\",\"uuid-2\"]")
        @Size(min = 2, message = "최소 2명 이상이 대화에 참여해야 합니다.")
        List<@NotNull(message = "잘못된 ID 입니다.") UUID> participantIds
) {
}
