package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "유저네임은 공백일 수 없습니다.")
    @Size(min = 3, max = 50, message = "유저네임은 3자 이상 50자 이하이어야 합니다.")
    String newUsername,

    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다")
    @Size(min = 5, max = 100, message = "이메일은 5자 이상 100자 이하이어야 합니다.")
    String newEmail,

    @NotBlank(message = "비밀번호는 공백일 수 없습니다")
    @Size(min = 5, max = 60, message = "비밀번호는 5자 이상 60자 이하이어야 합니다.")
    String newPassword
) {

}
