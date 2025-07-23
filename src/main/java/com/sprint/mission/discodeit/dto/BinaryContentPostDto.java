package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "첨부파일 생성")
public record BinaryContentPostDto(
        @Schema(description = "파일 이름", example = "file1")
        String fileName,
        @Schema(description = "파일 타입", example = "image/jpeg")
        String contentType,
        @Schema(description = "파일", format = "byte")
        byte[] content
) {
}
