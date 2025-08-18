package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널명은 비어 있을 수 없습니다")
    @Size(max = 12, message = "채널명은 최대 12자까지 허용됩니다")
    String name,

    @Size(max = 255, message = "채널 설명은 최대 255자까지 허용됩니다")
    String description
) {

}
