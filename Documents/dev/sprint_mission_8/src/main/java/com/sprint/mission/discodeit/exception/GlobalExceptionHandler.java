package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외
    @ExceptionHandler(DiscodeitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleCustomException (DiscodeitException exception) {
        int status = exception.getErrorCode().getStatus();
        ErrorResponse body = ErrorResponse.of(
                exception.getErrorCode(),
                exception.getDetails(),
                exception.getClass().getSimpleName());
        return ResponseEntity.status(status).body(body);
    }

    // 파라미터/ 경로 검증 실패 예외
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException (ConstraintViolationException exception) {
        Map<String, Object> details = new LinkedHashMap<>();
        exception.getConstraintViolations()
                .forEach(violation -> details.put(violation.getPropertyPath().toString(), violation.getMessage()));
        int status = HttpStatus.BAD_REQUEST.value();
        ErrorResponse body = ErrorResponse.ofValidationError(
                "요청 파라미터 검증에 실패했습니다.",
                details,
                status,
                exception.getClass().getSimpleName());
        return ResponseEntity.status(status).body(body);
    }

    // @Valid @RequestBody 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(MethodArgumentNotValidException ex) {
        Map<String, Object> details = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> details.put(err.getField(), err.getDefaultMessage()));

        int status = HttpStatus.BAD_REQUEST.value();
        ErrorResponse body = ErrorResponse.ofValidationError(
                "요청 본문 검증에 실패했습니다.",
                details, status,
                ex.getClass().getSimpleName());
        return ResponseEntity.status(status).body(body);
    }

    // 알 수 없는 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception exception) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        ErrorResponse body = ErrorResponse.ofInternalServerError(exception.getClass().getSimpleName());
        return ResponseEntity.status(status).body(body);
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<String> handleException(Exception e) {
//        e.printStackTrace();
//        return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
//    }
}
