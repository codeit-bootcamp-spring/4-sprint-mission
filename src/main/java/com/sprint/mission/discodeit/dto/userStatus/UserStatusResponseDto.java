package com.sprint.mission.discodeit.dto.userStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
public class UserStatusResponseDto {
    @Schema(description = "사용자 상태(UserStatus) ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID id;

    @Schema(description = "사용자 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID userId;

    @Schema(description = "생성일", example = "2025-07-08T09:55:59.735Z")
    private final Instant lastConnectedAt;
}
