package com.sprint.mission.discodeit.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorResponse {
    private String code;
    private String message;
    private Instant timestamp;
    private Map<String, Object> details;
    private String exceptionType;
    private int status;

    public ErrorResponse(String code, String message, Instant timestamp, Map<String, Object> details, String exceptionType, int status) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
        this.exceptionType = "unknown";
        this.status = status;

    }

    public static ErrorResponse of(ErrorCode errorCode, Map<String, Object> details, String exceptionType) {
        return new ErrorResponse(
                errorCode.getErrorCode().toString(),
                errorCode.getMessage(),
                Instant.now(),
                details != null ? details : new HashMap<>(),
                "unknown",
                errorCode.getStatus()
        );
    }

    public static ErrorResponse ofValidationError(String message,
                                                  Map<String, Object> details,
                                                  int status,
                                                  String exceptionType) {
        return new ErrorResponse(
                "VALIDATION_ERROR",
                message,
                Instant.now(),
                details != null ? details : new HashMap<>(),
                exceptionType !=null ? exceptionType : "unknown",
                status
        );
    }

    public static ErrorResponse ofInternalServerError (String exceptionType) {
        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다.",
                Instant.now(),
                new HashMap<>(),
                exceptionType != null ? exceptionType : "unknown",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }
}
