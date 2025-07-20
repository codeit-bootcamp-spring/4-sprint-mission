package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;

import java.time.Instant;

public class ErrorResponseMapper {

    public static ErrorResponseDto toDto(HttpStatus status, String message) {
        return new ErrorResponseDto(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
    }
}
