package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(type = "object", description = "로그인 정보")
public record LoginRequest(
        @Schema(description = "유저 이름", example = "Woody")
        @NotBlank(message = "잘못된 유저네임입니다.")
        String username,
        @Schema(description = "패스워드", example = "woody1234")
        @NotBlank(message = "잘못된 패스워드입니다.")
        String password
) {
}
