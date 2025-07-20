package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Schema(description = "에러 응답 DTO")
public class ErrorResponseDto {

    @Schema(description = "에러 발생 시각", type = "string", format = "date-time", example = "2025-07-09T15:30:00")
    private final Instant timestamp;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private final int status;

    @Schema(description = "에러 타입 또는 상태 메시지", example = "Bad Request")
    private final String error;

    @Schema(description = "상세 에러 메시지", example = "Invalid user ID format")
    private final String message;
}
