package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.user.ValidationFailedException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException ex) {
    HttpStatus status = mapErrorCodeToHttpStatus(ex.getErrorCode());

    ErrorResponse errorResponse =
        new ErrorResponse(
            ex.getTimestamp(),
            ex.getErrorCode().name(),
            ex.getErrorCode().getMessage(),
            ex.getDetails() != null ? ex.getDetails() : Collections.emptyMap(),
            ex.getClass().getSimpleName(),
            status.value());

    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.badRequest().build(); // 400
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    Map<String, Object> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing));

    ValidationFailedException exception = new ValidationFailedException(errors);

    HttpStatus status = HttpStatus.BAD_REQUEST;
    ErrorResponse errorResponse =
        new ErrorResponse(
            exception.getTimestamp(),
            exception.getErrorCode().name(),
            exception.getErrorCode().getMessage(),
            exception.getDetails(),
            exception.getClass().getSimpleName(),
            status.value());

    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    ErrorResponse errorResponse =
        new ErrorResponse(
            Instant.now(),
            "INTERNAL_SERVER_ERROR",
            ex.getMessage() != null ? ex.getMessage() : "Internal server error",
            Collections.emptyMap(),
            ex.getClass().getSimpleName(),
            status.value());

    return new ResponseEntity<>(errorResponse, status);
  }

  private HttpStatus mapErrorCodeToHttpStatus(ErrorCode errorCode) {
    return switch (errorCode) {
      case USER_NOT_FOUND,
              CHANNEL_NOT_FOUND,
              MESSAGE_NOT_FOUND,
              BINARY_CONTENT_NOT_FOUND,
              READ_STATUS_NOT_FOUND,
              USER_STATUS_NOT_FOUND ->
          HttpStatus.NOT_FOUND;

      case DUPLICATE_USER,
              DUPLICATE_MESSAGE,
              DUPLICATE_BINARY_CONTENT,
              DUPLICATE_READ_STATUS,
              DUPLICATE_USER_STATUS ->
          HttpStatus.CONFLICT;

      case PRIVATE_CHANNEL_UPDATE, INVALID_USER_ARGUMENT, VALIDATION_FAILED ->
          HttpStatus.BAD_REQUEST;

      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
