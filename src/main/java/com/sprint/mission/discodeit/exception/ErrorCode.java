package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */

    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(400, "Bad Request Exception"),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(400, "Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(400, " Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(400, "Missing Servlet RequestParameter Exception"),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(400, "JsonParseException"),

    // 권한이 없음
    FORBIDDEN_ERROR(403, "Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(404,  "Not Found Exception"),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(404, "Null Point Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(404,"handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(404,"Header에 데이터가 존재하지 않는 경우 "),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(500, "Internal Server Error Exception"),

    /**
     * ******************************* File I/O Error CodeList ***************************************
     */
    FILE_IO_ERROR(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "파일 입출력 중 오류가 발생했습니다."),
    FILE_CLASS_NOT_FOUND(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "직렬화 클래스 로딩 중 오류가 발생했습니다.");

    @Getter
    private final int statusCode;

    @Getter
    private final String message;

    ErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
