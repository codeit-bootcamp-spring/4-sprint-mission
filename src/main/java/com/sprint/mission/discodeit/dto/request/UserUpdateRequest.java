package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
        @Schema(description = "새로운 유저 이름", example = "Buzz")
        @NotBlank(message = "유저 이름은 필수 항목입니다.")
        String newUsername,
        @Schema(description = "새로운 유저 이메일 | 중복 불가", example = "buzz@codeit.com", format = "email")
        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "이메일의 형식이 아닙니다.")
        String newEmail,
        @Schema(description = "새로운 비밀번호", example = "buzz1234")
        @NotBlank(message = "비밀번호는 필수 항목입니다.")
        String newPassword
) {
}
