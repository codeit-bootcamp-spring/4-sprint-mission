package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "수정할 User 정보")
public record UserUpdateRequest(
        @Schema(description = "새로운 유저 이름", example = "Buzz")
        String newUsername,
        @Schema(description = "새로운 유저 이메일 | 중복 불가", example = "buzz@codeit.com", format = "email")
        String newEmail,
        @Schema(description = "새로운 비밀번호", example = "buzz1234")
        String newPassword
) {
}
