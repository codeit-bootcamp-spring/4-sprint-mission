package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record BinaryContent(
        @Schema(description = "바이너리 파일 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "바이너리 파일 생성 시간", example = "2025-07-10T02:15:30.123Z")
        Instant createdAt,
        @Schema(description = "바이너리 파일 이름", example = "profile.png")
        String fileName,
        @Schema(description = "파일 크기 (bytes 단위)", example = "204800")
        Long size,
        @Schema(description = "파일 MIME 타입", example = "image/png")
        String contentType,
        @Schema(description = "바이너리 파일 데이터")
        byte[] bytes
) {
}
