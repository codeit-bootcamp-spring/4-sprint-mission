package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorResponseDto {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
}
