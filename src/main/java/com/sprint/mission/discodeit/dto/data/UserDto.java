package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        @Schema(description = "유저 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "유저 생성 시간", example = "2025-07-10T02:15:30.123Z")
        Instant createdAt,
        @Schema(description = "최근 유저 업데이트 시간", example = "2025-07-10T02:15:30.123Z")
        Instant updatedAt,
        @Schema(description = "유저 이름", example = "Minsu")
        String username,
        @Schema(description = "유저 이메일",example = "minsu@examle.com")
        String email,
        @Schema(description = "프로필 이미지 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID profileId,
        @Schema(description = "온라인 여부", example = "true")
        Boolean online
) {
}
