package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "반환 시 유저 정보")
public record UserResponseDto(
        @Schema(description = "유저 ID", format = "uuid")
        UUID id,
        @Schema(description = "유저 이름", example = "Woody")
        String username,
        @Schema(description = "유저 이메일", example = "woody@codeit.com", format = "email")
        String email,
        @Schema(description = "유저 프로필 이미지 ID", format = "uuid")
        UUID profile,
        @Schema(description = "유저 상태 정보", implementation = UserStatusResponseDto.class)
        UserStatusResponseDto userStatus
) {
}
