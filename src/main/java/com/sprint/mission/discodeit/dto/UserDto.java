package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "유저 정보")
public record UserDto(
        @Schema(description = "유저 ID", format = "uuid")
        UUID id,
        @Schema(description = "유저 최초 생성 시각", format = "date-time")
        Instant createdAt,
        @Schema(description = "유저 최종 업데이트 시각", format = "date-time")
        Instant updatedAt,
        @Schema(description = "유저 이름", example = "Woody")
        String username,
        @Schema(description = "유저 이메일", example = "woody@codeit.com", format = "email")
        String email,
        @Schema(description = "유저 프로필 이미지 ID", format = "uuid")
        UUID profileId,
        @Schema(description = "온라인 상태", example = "true")
        Boolean online
) {
}

