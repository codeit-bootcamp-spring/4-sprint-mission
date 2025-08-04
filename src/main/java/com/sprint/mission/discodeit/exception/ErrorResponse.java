package com.sprint.mission.discodeit.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
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

    private ErrorResponse(List<FieldError> fieldErrors, List<ConstraintViolationError> violationErrors) {
        this.fieldErrors = fieldErrors;
        this.violationErrors = violationErrors;
    }

    private ErrorResponse(ExceptionCode exceptionCode, Instant timestamp, String message) {
        this.status = exceptionCode.getStatus();
        this.timestamp = timestamp;
        this.message = message;
    }

    public static ErrorResponse of(BindingResult bindingResult) {
        return new ErrorResponse(FieldError.of(bindingResult), null);
    }

    public static ErrorResponse of(Set<ConstraintViolation<?>> violations) {
        return new ErrorResponse(null, ConstraintViolationError.of(violations));
    }

    public static ErrorResponse of(BusinessLogicException businessLogicException) {
        return new ErrorResponse(businessLogicException.getExceptionCode(), Instant.now(), businessLogicException.getMessage());
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

