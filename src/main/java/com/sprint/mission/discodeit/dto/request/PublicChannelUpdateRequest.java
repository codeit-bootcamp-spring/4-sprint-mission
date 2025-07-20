package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PublicChannelUpdateRequest(

    @Schema(description = "새로운 채널 이름")
    String newName,

    @Schema(description = "새로운 채널 설명")
    String newDescription
) {

}
