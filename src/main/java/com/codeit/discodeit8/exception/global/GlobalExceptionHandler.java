package com.codeit.discodeit8.exception.global;

import com.codeit.discodeit8.exception.binarycontent.BinaryContentException;
import com.codeit.discodeit8.exception.binarycontentstorage.BinaryContentStorageException;
import com.codeit.discodeit8.exception.channel.ChannelException;
import com.codeit.discodeit8.exception.message.MessageException;
import com.codeit.discodeit8.exception.readstatus.ReadStatusException;
import com.codeit.discodeit8.exception.user.UserException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(UserException ex) {

    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus()
    );
    log.warn("유저 오류: {}", errorResponse);
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(BinaryContentException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentException(BinaryContentException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus()
    );
    log.warn("바이너리 컨텐츠 오류: {}", errorResponse);
    return ResponseEntity.status(errorResponse.status())
        .body(errorResponse);
  }

  @ExceptionHandler(ChannelException.class)
  public ResponseEntity<ErrorResponse> handleChannelException(ChannelException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus()
    );
    log.warn("채널 오류: {}", errorResponse);
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(MessageException.class)
  public ResponseEntity<ErrorResponse> handleMessageException(MessageException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus()
    );
    log.warn("메세지 오류: {}", errorResponse);
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(BinaryContentStorageException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentStorageException(
      BinaryContentStorageException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus()
    );
    log.warn("바이너리 컨텐츠 저장소 오류: {}", errorResponse);
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(ReadStatusException.class)
  public ResponseEntity<ErrorResponse> handleReadStatusException(ReadStatusException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus()
    );
    log.warn("읽기 상태 오류: {}", errorResponse);
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now(),
        "FAILED_VALIDATION_ERROR",
        "검증이 필요한 필드가 검증에 실패했습니다.",
        Map.of("fieldErrors", errors),
        ex.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    log.warn("필드 검증 오류: {}", errorResponse);

    return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleUnexpectedError(RuntimeException ex) {
    return ResponseEntity.internalServerError().body(Map.of("error", "예상치 못한 오류가 발생했습니다."));
  }
}