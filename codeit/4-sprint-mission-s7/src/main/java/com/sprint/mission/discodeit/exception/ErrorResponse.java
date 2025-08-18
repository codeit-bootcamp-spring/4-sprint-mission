package com.sprint.mission.discodeit.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    /** 예외 발생 시각 */
    private final Instant timestamp;

    /** 에러 코드 문자열 (예: "USER_NOT_FOUND") */
    private final String code;

    /** 사용자 메시지 */
    private final String message;

    /** 추가 컨텍스트 (ex: userId, channelId 등) */
    private final Map<String, Object> details;

    /** 예외 클래스명 (예: UserNotFoundException) */
    private final String exceptionType;

    /** HTTP 상태 코드(int) */
    private final int status;

    /** DiscodeitException -> ErrorResponse 변환 */
    public static ErrorResponse from(DiscodeitException ex, int status) {
        Map<String, Object> safeDetails = ex.getDetails() == null
                ? Collections.emptyMap()
                : ex.getDetails();

        return ErrorResponse.builder()
                .timestamp(ex.getTimestamp())
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .details(safeDetails)
                .exceptionType(ex.getClass().getSimpleName())
                .status(status)
                .build();
    }

    /** 임의의 값들로 직접 생성할 때 사용 */
    public static ErrorResponse of(String code, String message,
                                   Map<String, Object> details,
                                   String exceptionType,
                                   int status) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .code(code)
                .message(message)
                .details(details == null ? Collections.emptyMap() : Collections.unmodifiableMap(details))
                .exceptionType(exceptionType)
                .status(status)
                .build();
    }
}
