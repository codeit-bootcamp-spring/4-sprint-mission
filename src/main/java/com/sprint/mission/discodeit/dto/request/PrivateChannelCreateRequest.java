package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @Schema(description = "채널에 참여할 사용자 ID 목록")
    List<UUID> participantIds
) {

}
