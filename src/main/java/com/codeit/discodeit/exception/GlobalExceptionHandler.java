package com.codeit.discodeit.exception;

import com.codeit.discodeit.exception.dto.ErrorResponseDto;
import com.codeit.discodeit.exception.exception.DuplicateChannelException;
import com.codeit.discodeit.exception.exception.DuplicateReadStatusException;
import com.codeit.discodeit.exception.exception.DuplicateUserException;
import com.codeit.discodeit.exception.exception.NoFindBinaryContent;
import com.codeit.discodeit.exception.exception.NoFindChannelException;
import com.codeit.discodeit.exception.exception.NoFindMessageException;
import com.codeit.discodeit.exception.exception.NoFindReadStatusException;
import com.codeit.discodeit.exception.exception.NoFindUserException;
import com.codeit.discodeit.exception.exception.NoFindUserStatusException;
import com.codeit.discodeit.exception.exception.PrivateChannelUpdateNotAllowedException;
import com.codeit.discodeit.exception.exception.WrongPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<ErrorResponseDto> handleDuplicateUserException(DuplicateUserException e) {
    ErrorResponseDto response = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(NoFindUserException.class)
  public ResponseEntity<ErrorResponseDto> handleNoFindUserException(NoFindUserException e) {
    ErrorResponseDto response = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(NoFindUserStatusException.class)
  public ResponseEntity<ErrorResponseDto> handleNoFindUserStatusException(
      NoFindUserStatusException e) {
    ErrorResponseDto response = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(WrongPasswordException.class)
  public ResponseEntity<ErrorResponseDto> handleWrongPasswordException(WrongPasswordException e) {
    ErrorResponseDto response = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(DuplicateChannelException.class)
  public ResponseEntity<ErrorResponseDto> handleDuplicateChannelException(
      DuplicateChannelException e) {
    ErrorResponseDto response = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(NoFindChannelException.class)
  public ResponseEntity<ErrorResponseDto> handleNoFindChannelException(NoFindChannelException e) {
    ErrorResponseDto response = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(PrivateChannelUpdateNotAllowedException.class)
  public ResponseEntity<ErrorResponseDto> handlePrivateChannelUpdateNotAllowedException(
      PrivateChannelUpdateNotAllowedException e
  ) {
    ErrorResponseDto responseDto = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
  }

  @ExceptionHandler(NoFindMessageException.class)
  public ResponseEntity<ErrorResponseDto> handleNoFindMessageException(NoFindMessageException e) {
    ErrorResponseDto responseDto = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
  }

  @ExceptionHandler(DuplicateReadStatusException.class)
  public ResponseEntity<ErrorResponseDto> handleDuplicateReadStatusException(
      DuplicateReadStatusException e
  ) {
    ErrorResponseDto responseDto = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
  }

  @ExceptionHandler(NoFindReadStatusException.class)
  public ResponseEntity<ErrorResponseDto> handleNoFindReadStatusException(
      NoFindReadStatusException e
  ) {
    ErrorResponseDto responseDto = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
  }

  @ExceptionHandler(NoFindBinaryContent.class)
  public ResponseEntity<ErrorResponseDto> handleNoFindBinaryContent(
      NoFindBinaryContent e
  ) {
    ErrorResponseDto responseDto = new ErrorResponseDto(e.getMessage(), e.getContent());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
  }

}