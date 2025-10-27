package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.constant.ClientRequestErrorCode;
import com.sprint.mission.discodeit.constant.ErrorCode;
import jakarta.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String message;
    private int status;
    private String code;
    private List<FieldError> errors;
    private List<ConstraintViolationError> violationErrors;

    private ErrorResponse(ErrorCode code, List<FieldError> errors, List<ConstraintViolationError> violationErrors) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = errors;
        this.violationErrors = violationErrors;
    }

    private ErrorResponse(String message) {
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST.value();
        this.code = HttpStatus.BAD_REQUEST.name();
        this.errors = new ArrayList<>();
        this.violationErrors = new ArrayList<>();
    }

    public static ErrorResponse of (String message) {
        return new ErrorResponse(message);
    }

    public static ErrorResponse of(ErrorCode code, BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult), new ArrayList<>());
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code, new ArrayList<>(), new ArrayList<>());
    }

    public static ErrorResponse ofFieldErrors(ErrorCode code, List<FieldError> errors) {
        return new ErrorResponse(code, errors, new ArrayList<>());
    }

    public static ErrorResponse ofViolationErrors(ErrorCode code, List<ConstraintViolationError> violationErrors) {
        return new ErrorResponse(code, new ArrayList<>(), violationErrors);
    }

    public static ErrorResponse of(MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : e.getValue().toString();
        final List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of(e.getName(), value, e.getErrorCode());
        return ErrorResponse.ofFieldErrors(ClientRequestErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH, errors);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        private static List<FieldError> of(String field, String value, String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of (BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ConstraintViolationError {
        private String propertyPath;
        private Object rejectedValue;
        private String reason;

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
