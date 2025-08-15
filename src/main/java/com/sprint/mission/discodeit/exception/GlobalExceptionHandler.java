package com.sprint.mission.discodeit.exception;

import jakarta.servlet.ServletException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
//@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleException(DiscodeitException e) {
    ErrorResponse errorResponse = new ErrorResponse(e);
    return new ResponseEntity<>(errorResponse, e.getErrorCode().getStatus());
  }

  //
  @ExceptionHandler(ServletException.class)
  public ResponseEntity<ErrorResponse> handleServletException(ServletException e) {
    log.warn("Servlet exception occurred: {}", e.getMessage());

    ErrorCode errorCode = ErrorCode.BAD_REQUEST;
    final ErrorResponse errorResponse = new ErrorResponse(
        errorCode.getCode(),
        e.getMessage(),
        Map.of("exceptionType", e.getClass().getSimpleName()),
        e.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );
    return new ResponseEntity<>(errorResponse, errorCode.getStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
    log.error("Validation failed: ", e);

    // get the detailed error messages
    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(fieldError ->
        details.put(fieldError.getField(), fieldError.getDefaultMessage()));

    final ErrorCode errorCode = ErrorCode.BAD_REQUEST;
    final ErrorResponse errorResponse = new ErrorResponse(
        errorCode.getCode(),
        errorCode.getMessage(),
        details,
        e.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );

    return new ResponseEntity<>(errorResponse, errorCode.getStatus());

  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("An unexpected error occurred: ", e);

    final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    final ErrorResponse errorResponse = new ErrorResponse(
        errorCode.getCode(),
        errorCode.getMessage(),
        Map.of(), // Don't expose internal details
        e.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );

    return new ResponseEntity<>(errorResponse, errorCode.getStatus());
  }
}
