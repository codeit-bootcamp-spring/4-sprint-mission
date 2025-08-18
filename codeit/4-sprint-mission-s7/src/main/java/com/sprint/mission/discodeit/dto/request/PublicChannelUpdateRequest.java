package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Size(max = 12, message = "채널명은 최대 12자까지 허용됩니다.")
    String newName,

    @Size(max = 255, message = "채널 설명은 최대 255자까지 허용됩니다")
    String newDescription
) {

}
