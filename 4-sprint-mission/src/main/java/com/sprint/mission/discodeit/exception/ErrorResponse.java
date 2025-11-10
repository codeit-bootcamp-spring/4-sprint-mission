package com.sprint.mission.discodeit.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Schema(description = "전역 예외 처리")
public class ErrorResponse {
    private List<FieldError> fieldErrors;
    private List<ConstraintViolationError> violationErrors;
    @Schema(description = "HTTP 상태코드", example = "400")
    private int status;
    @Schema(description = "발생 시각", format = "date-time")
    private Instant timestamp;
    @Schema(description = "에러 내용", example = "Bad Request!")
    private String message;
    private String code;
    private String exceptionType;
    private Map<String, Object> details;


    private ErrorResponse(List<FieldError> fieldErrors, List<ConstraintViolationError> violationErrors) {
        this.fieldErrors = fieldErrors;
        this.violationErrors = violationErrors;
    }

    private ErrorResponse(ErrorCode errorCode, Instant timestamp, String message) {
        this.status = errorCode.getStatus();
        this.timestamp = timestamp;
        this.message = message;
    }

    public ErrorResponse(int status, Instant timestamp, String message, String code, String exceptionType, Map<String, Object> details) {
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
        this.code = code;
        this.exceptionType = exceptionType;
        this.details = details;
    }

    public ErrorResponse(AuthenticationException exception) {
        this.status = HttpServletResponse.SC_UNAUTHORIZED;
        this.timestamp = Instant.now();
        this.message = exception.getMessage();
        this.details = Map.of("authorized", "권한이 없습니다.");
    }

    public ErrorResponse(AccessDeniedException exception) {
        this.status = HttpStatus.FORBIDDEN.value();
        this.timestamp = Instant.now();
        this.message = "권한이 없습니다.";
        this.details = Map.of("authorized", "권한이 없습니다.");
    }

    public static ErrorResponse of(BindingResult bindingResult) {
        return new ErrorResponse(FieldError.of(bindingResult), null);
    }

    public static ErrorResponse of(Set<ConstraintViolation<?>> violations) {
        return new ErrorResponse(null, ConstraintViolationError.of(violations));
    }

    public static ErrorResponse of(BusinessLogicException businessLogicException) {
        return new ErrorResponse(businessLogicException.getErrorCode(), Instant.now(), businessLogicException.getMessage());
    }

    public static ErrorResponse of(DiscodeitException discodeitException) {
        return new ErrorResponse(discodeitException.getErrorCode().getStatus(), Instant.now(), discodeitException.getMessage(), discodeitException.getErrorCode().name(), discodeitException.getClass().getSimpleName(), discodeitException.getDetails());
    }

    public static ErrorResponse of(AuthenticationException exception) {
        return new ErrorResponse(exception);
    }

    public static ErrorResponse of(AccessDeniedException exception) {
        return new ErrorResponse(exception);
    }

    @Getter
    @Schema(description = "DTO 검증 실패")
    public static class FieldError {
        @Schema()
        private final String field;
        @Schema()
        private final Object rejectedValue;
        @Schema()
        private final String reason;

        private FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @Schema(description = "URI 변수 검증 실패")
    public static class ConstraintViolationError {
        @Schema()
        private final String propertyPath;
        @Schema()
        private final Object rejectedValue;
        @Schema()
        private final String reason;

        private ConstraintViolationError(String propertyPath, Object rejectedValue, String reason) {
            this.propertyPath = propertyPath;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<ConstraintViolationError> of(Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                    .map(cv -> new ConstraintViolationError(
                            cv.getPropertyPath().toString(),
                            cv.getInvalidValue(),
                            cv.getMessage()))
                    .collect(Collectors.toList());
        }
    }
}

