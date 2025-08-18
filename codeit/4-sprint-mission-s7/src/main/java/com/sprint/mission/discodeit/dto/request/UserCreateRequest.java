package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotBlank(message = "사용자명은 비어 있을 수 없습니다")
    @Size(max = 50, message = "사용자명은 최대 50자까지 허용됩니다")
    String username,

    @NotBlank(message = "이메일은 비어 있을 수 없습니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Size(max = 100, message = "이메일은 최대 100자까지 허용됩니다")
    String email,

    @NotBlank(message = "비밀번호는 비어 있을 수 없습니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다")
    String password
) {

}
