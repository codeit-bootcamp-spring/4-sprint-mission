package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "첨부파일 DTO")
public class BinaryContentDto {

    @Schema(description = "첨부 파일 정보")
    public record BinaryContentRequest(
            @Schema(description = "파일 명", example = "cat.png", format = "string")
            String fileName,
            @Schema(description = "파일 타입", example = "image/png", format = "string")
            String contentType,
            @Schema(description = "파일 정보", type = "string", format = "binary")
            byte[] bytes
    ) {

    }

    @Schema(description = "첨부 파일 응답 정보")
    public record BinaryContentResponse(
            @Schema(description = "파일 ID", example = "123e4567-e89b-12d3-a456-426614174000")
            UUID id,

            @Schema(description = "파일 업로드 시각", example = "2025-07-10T10:54:00Z")
            Instant createdAt,

            @Schema(description = "파일 명", example = "cat.png")
            String fileName,

            @Schema(description = "파일 크기 (bytes)", example = "1024")
            Long size,

            @Schema(description = "파일 타입", example = "image/png")
            String contentType,

            @Schema(description = "파일 내용 (Base64 인코딩된 문자열)", format = "byte")
            String bytes
    ) {

    }

    @Schema(description = "첨부 파일 목록 응답")
    public record ResponseDtos(
            @Schema(description = "첨부 파일 정보 목록")
            List<BinaryContentResponse> binaryContentResponses
    ) {

    }
}
