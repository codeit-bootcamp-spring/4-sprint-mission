package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Private Channel 생성 정보")
public record PrivateChannelCreateRequest(
        @Schema(description = "비공개 채널 참가자들의 ID", format = "uuid", example = "[\"uuid-1\",\"uuid-2\"]")
        List<UUID> participantIds
) {
}
