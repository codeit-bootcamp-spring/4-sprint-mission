package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserCreateRequest(
        @Schema(description = "유저 이름", example = "woody")
        String username,
        @Schema(description = "유저 이메일", example = "woody@email.com")
        String email,
        @Schema(description = "유저 비밀번호", example = "woody1234")
        String password
) {
}
