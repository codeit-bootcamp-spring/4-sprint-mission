package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "첨부 파일 정보")
public record BinaryContentDto(
        @Schema(description = "첨부 파일 ID", format = "uuid")
        UUID id,
        @Schema(description = "첨부파일 크기", format = "int64")
        Long size,
        @Schema(description = "첨부파일 이름", example = "file1")
        String filename,
        @Schema(description = "첨부파일 종류", example = "image/jpeg")
        String contentType,
        @Schema(description = "첨부파일 상태", allowableValues = {"SUCCESS", "FAIL", "PROCESSING"})
        BinaryContentStatus status
) {
}
