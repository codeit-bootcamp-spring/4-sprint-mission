package com.sprint.mission.discodeit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientRequestErrorCode implements ErrorCode {

    INVALID_INPUT_VALUE(400, "CLIENT_REQUEST_001", "유효하지 않은 입력 값"),
    METHOD_ARGUMENT_TYPE_MISMATCH(400, "CLIENT_REQUEST_002", "요청 파라미터 타입 오류"),
    MISSING_PARAMETER(400, "CLIENT_REQUEST_003", "필수 요청 파라미터가 누락"),
    MISMATH_REQUEST_METHOD(400, "CLIENT_REQUEST_004", "요청 메소드 오류"),
    VALIDATION_FAILED(400, "CLIENT_REQUEST_005", "유효하지 않은 요청 값"),
    CONSTRAINT_VIOLATION(400, "CLIENT_REQUEST_006", "제약 조건을 위반"),
    METHOD_NOT_ALLOWED(405, "CLIENT_REQUEST_007", "허용되지 않은 HTTP 메서드입니다"),
    UKNOWN_ERROR(500, "CLIENT_REQUEST_008", "UNKNOWN_ERROR");;

    private final int status;
    private final String code;
    private final String message;
}
