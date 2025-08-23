package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record PublicChannelCreateRequest(
        @NotBlank(message = "채널명은 공백일 수 없습니다.")
        @Length(max = 100)
        String name,

        @Length(max = 500)
        String description
) {

}
