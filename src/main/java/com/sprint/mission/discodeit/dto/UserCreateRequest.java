package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "object", description = "User 생성 정보")
public record UserCreateRequest(
        @Schema(description = "유저이름 | 아이디의 역할 수행", example = "Woody")
        String username,
        @Schema(description = "패스워드", example = "woody1234")
        String password,
        @Schema(description = "이메일 | 동시에 하나만 존재 가능", example = "woody@codeit.com", format = "email")
        String email
) {
}
