package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @Size(max = 50, message = "사용자명은 최대 50자까지 허용됩니다")
    String newUsername,

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Size(max = 100, message = "이메일은 최대 100자까지 허용됩니다")
    String newEmail,

    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다")
    String newPassword
) {

}
