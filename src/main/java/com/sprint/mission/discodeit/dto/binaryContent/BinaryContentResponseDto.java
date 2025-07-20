package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
public class BinaryContentResponseDto {
    @Schema(description = "바이너리 컨텐츠 ID", example = "9682efac-93bb-4975-9ca7-ed566580f466")
    private final UUID id;

    @Schema(description = "바이너리 파일")
    private final byte[] bytes;

    @Schema(description = "바이너리 컨텐츠 타입", example = "MESSAGE")
    private final BinaryContentType type;

    @Schema(description = "원본 파일명", example = "joy.png")
    private final String fileName;

    @Schema(description = "콘텐츠 타입", example = "image/png")
    private final String contentType;

    @Schema(description = "파일 크기 (바이트)", example = "335919")
    private final long size;

    @Schema(description = "생성일", example = "2025-07-08T09:55:59.735Z")
    private final Instant createdAt;
}
