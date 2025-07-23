package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.PresenceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "로그인 성공")
public record UserLoginResponseDto(
        @Schema(description = "로그인한 유저 ID", format = "uuid")
        UUID id,
        @Schema(description = "유저 프로필 ID", format = "uuid")
        UUID profileId,
        @Schema(description = "유저 이름", example = "Woody")
        String username,
        @Schema(description = "유저 이메일", example = "woody@codeit.com", format = "email")
        String email,
        @Schema(description = "유저 상태정보", example = "ONLINE")
        PresenceStatus presenceStatus
) {
}
