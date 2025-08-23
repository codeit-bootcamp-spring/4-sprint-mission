package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserCreateRequest(
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        @Length(max = 50)
        String username,

        @Email
        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        @Length(max = 100)
        String email,

        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        @Length(max = 60)
        String password
) {

}
