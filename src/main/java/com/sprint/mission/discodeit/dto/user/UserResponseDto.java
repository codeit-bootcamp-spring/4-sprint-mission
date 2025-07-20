package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
public class UserResponseDto {
    @Schema(description = "사용자 ID", example = "82170d94-e9e6-45df-874a-f4fb949f0835")
    private final UUID id;

    @Schema(description = "사용자명", example = "Mingguriguri")
    private final String username;

    @Schema(description = "이메일", example = "abc@gmail.com")
    private final String email;

    @Schema(description = "바이너리 컨텐츠 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID profileId;

    @Schema(description = "사용자 온라인 여부", example = "true")
    private final boolean isOnline;

    @Schema(description = "생성일", example = "2025-07-08T09:55:59.735Z")
    private final Instant createdAt;

    @Schema(description = "수정일", example = "2025-07-08T09:55:59.735Z")
    private final Instant updatedAt;
}
