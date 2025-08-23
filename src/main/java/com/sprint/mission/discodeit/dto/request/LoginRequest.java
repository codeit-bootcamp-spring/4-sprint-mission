package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        @Length(max = 50)
        String username,

        @NotBlank
        @Length(max = 60)
        String password
) {

}
