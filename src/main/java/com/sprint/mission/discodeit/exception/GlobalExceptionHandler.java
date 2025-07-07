package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UncheckedIOException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNotFound(NoSuchElementException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleBadRequest(IllegalArgumentException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    /**
     * Multipart file을 바이트로 읽어올 수 없을 때 발생하는 예외입니다.
     * 어떤 응답 코드로 반환하는 게 좋을까 하고 찾아보다가 UNSUPPORTED_MEDIA_TYPE가 미디어 타입과 관련된 응답이라 생각하여 UNSUPPORTED_MEDIA_TYPE으로 설정했습니다.
     * 혹시 더 나은 응답 코드가 있다면 알려주시면 감사하겠습니다!
     */
    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity handleBadRequest(UncheckedIOException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponseDto);
    }
}
