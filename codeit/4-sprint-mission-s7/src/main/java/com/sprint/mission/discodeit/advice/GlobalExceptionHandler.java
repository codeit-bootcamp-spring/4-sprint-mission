package com.sprint.mission.discodeit.advice;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 커스텀 예외
  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeit(DiscodeitException discodeitException) {
    int status = mapStatus(discodeitException.getErrorCode());
    return ResponseEntity.status(status).body(ErrorResponse.from(discodeitException, status));
  }

  // 검증/바인딩 예외 (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
    Map<String, Object> details = methodArgumentNotValidException.getBindingResult().getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    err -> String.valueOf(err.getDefaultMessage()),
                    (a, b) -> b));
    DiscodeitException discodeitException = new DiscodeitException(
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.VALIDATION_FAILED.getMessage(),
            details,
            methodArgumentNotValidException
    );
    return handleDiscodeit(discodeitException);
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleBind(BindException bindException) {
    Map<String, Object> details = bindException.getBindingResult().getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    err -> String.valueOf(err.getDefaultMessage()),
                    (a, b) -> b));
    DiscodeitException discodeitException = new DiscodeitException(
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.VALIDATION_FAILED.getMessage(),
            details,
            bindException
    );
    return handleDiscodeit(discodeitException);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException constraintViolationException) {
    Map<String, Object> details = constraintViolationException.getConstraintViolations()
            .stream()
            .collect(Collectors.toMap(
                    v -> v.getPropertyPath().toString(),
                    v -> v.getMessage(),
                    (a, b) -> b));
    DiscodeitException discodeitException = new DiscodeitException(
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.VALIDATION_FAILED.getMessage(),
            details,
            constraintViolationException
    );
    return handleDiscodeit(discodeitException);
  }

  // 요청 포맷/파라미터 오류
  @ExceptionHandler({
          HttpMessageNotReadableException.class,
          MissingServletRequestParameterException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequestInfra(Exception exception) {
    int status = 400;
    ErrorResponse body = ErrorResponse.of(
            ErrorCode.INVALID_REQUEST.getCode(),
            exception.getMessage(),
            Collections.emptyMap(),
            exception.getClass().getSimpleName(),
            status
    );
    return ResponseEntity.status(status).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleException(IllegalArgumentException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleException(NoSuchElementException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
  }

  // ErrorCode -> HTTP 상태(int) 매핑
  private int mapStatus(ErrorCode code) {
    return switch (code) {
      // 400
      case INVALID_REQUEST, VALIDATION_FAILED,
           PRIVATE_CHANNEL_UPDATE,
           INSUFFICIENT_PARTICIPANTS,
           BINARY_FILENAME_BLANK, BINARY_BYTES_EMPTY, BINARY_CONTENT_TYPE_BLANK
              -> 400;

      // 401
      case UNAUTHORIZED, AUTHENTICATION_FAILED -> 401;

      // 403
      case FORBIDDEN -> 403;

      // 404
      case USER_NOT_FOUND,
           CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND,
           BINARY_CONTENT_NOT_FOUND -> 404;

      // 409
      case RESOURCE_CONFLICT, DUPLICATE_USER -> 409;

      // 500 (기본)
      default -> 500;
    };
  }
}
