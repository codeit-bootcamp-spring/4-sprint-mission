package com.codeit.discodeit.exception;

import com.codeit.discodeit.exception.dto.ErrorResponseDto;
import com.codeit.discodeit.exception.exception.BinaryContentNotFoundException;
import com.codeit.discodeit.exception.exception.BinaryContentStorageException;
import com.codeit.discodeit.exception.exception.BusinessException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<?> handleBusinessException(BusinessException ex) {
    ErrorCode errorCode = ex.getErrorCode();
    ErrorResponseDto response = new ErrorResponseDto(errorCode.name(), errorCode.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
  @ExceptionHandler(BinaryContentNotFoundException.class)
  public ResponseEntity<?> handleBinaryContentNotFound(BinaryContentNotFoundException ex) {
    return ResponseEntity.status(404).body(Map.of("error", "파일을 찾을 수 없습니다."));
  }

  @ExceptionHandler(BinaryContentStorageException.class)
  public ResponseEntity<?> handleStorageError(BinaryContentStorageException ex) {
    return ResponseEntity.status(500).body(Map.of("error", "파일 처리 중 문제가 발생했습니다."));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleUnexpectedError(RuntimeException ex) {
    return ResponseEntity.internalServerError().body(Map.of("error", "예상치 못한 오류가 발생했습니다."));
  }
}