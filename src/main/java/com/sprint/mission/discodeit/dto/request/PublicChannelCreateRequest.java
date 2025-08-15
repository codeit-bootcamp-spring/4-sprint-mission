package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(

    @NotBlank(message = "채널 이름은 공백일 수 없습니다.")
    @Size(min = 3, max = 100, message = "채널 이름은 3자 이상 100자 이하이어야 합니다.")
    String name,

    @Size(max = 500, message = "채널 이름은 500자 이하이어야 합니다.")
    String description
) {

}
