package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity illegalArgumentExceptionHandler(IllegalArgumentException e) {
    ErrorResponseDto errorResponse =
        new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity nullPointerExceptionHandler(NullPointerException e) {
    ErrorResponseDto errorResponse =
        new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException e) {
    ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(),
        e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleMessageNotFound(MessageNotFoundException e) {
    ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(),
        e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
}
