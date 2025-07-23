package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "object", description = "로그인 정보")
public record LoginRequest(
        @Schema(description = "유저 이름", example = "Woody")
        String username,
        @Schema(description = "패스워드", example = "woody1234")
        String password
) {
}
