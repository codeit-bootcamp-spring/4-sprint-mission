package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PublicChannelCreateRequest(
    @Schema(description = "채널 이름")
    String name,

    @Schema(description = "채널 설명")
    String description
) {

}
