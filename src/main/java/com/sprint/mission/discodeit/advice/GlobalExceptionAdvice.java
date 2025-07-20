package com.sprint.mission.discodeit.advice;

import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.FileAccessException;
import com.sprint.mission.discodeit.response.CommonResponse;
import com.sprint.mission.discodeit.response.FieldErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<?>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
                .status(code.getStatusCode())
                .body(CommonResponse.fail(code.getStatusCode(), code.getMessage(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<List<FieldErrorResponse>>> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new FieldErrorResponse(err.getField(), err.getDefaultMessage()))
                .toList();
        return ResponseEntity
                .status(ErrorCode.INVALID_TYPE_VALUE.getStatusCode())
                .body(CommonResponse.fail(
                        ErrorCode.INVALID_TYPE_VALUE.getStatusCode(), "유효성 검사 실패", errors
                ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<CommonResponse<?>> handleNotFound(NoSuchElementException e) {
        // 질문: 404 NOT FOUND는 body가 없는데 code.getMessages를 가져와야 하나요? 또는 가져오지 않아도 괜찮은가요?
        ErrorCode code = ErrorCode.NOT_FOUND_ERROR;
        return ResponseEntity
                .status(code.getStatusCode())
                .body(CommonResponse.fail(code.getStatusCode(), code.getMessage(), e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleBadRequest(IllegalArgumentException e) {
        ErrorCode code = ErrorCode.BAD_REQUEST_ERROR;
        return ResponseEntity
                .status(code.getStatusCode())
                .body(CommonResponse.fail(code.getStatusCode(), code.getMessage(), e.getMessage()));
    }

    @ExceptionHandler(FileAccessException.class)
    public ResponseEntity handleFileAccessException(FileAccessException e) {
        ErrorCode code = e.getExceptionCode();
        return ResponseEntity
                .status(code.getStatusCode())
                .body(CommonResponse.fail(code.getStatusCode(), code.getMessage(), e.getMessage()));
    }

    // 나머지 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<String>> handleAll(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail(500, "알 수 없는 오류가 발생했습니다.", e.toString()));
    }
}
