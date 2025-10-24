package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(type = "object", description = "User 생성 정보")
public record UserCreateRequest(
        @Schema(description = "유저이름 | 아이디의 역할 수행", example = "Woody")
        @NotBlank(message = "올바르지 않은 유저이름입니다.")
        String username,
        @Schema(description = "패스워드", example = "woody1234")
        @NotBlank(message = "올바르지 않은 패스워드입니다.")
        String password,
        @Schema(description = "이메일 | 동시에 하나만 존재 가능", example = "woody@codeit.com", format = "email")
        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수항목입니다.")
        String email
) {
}
