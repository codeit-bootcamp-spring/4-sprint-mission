package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorResponseDto;
import com.sprint.mission.discodeit.mapper.ErrorResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseMapper.toDto(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleNoSuchElementException(NoSuchElementException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseMapper.toDto(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseMapper.toDto(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));
    }
}
