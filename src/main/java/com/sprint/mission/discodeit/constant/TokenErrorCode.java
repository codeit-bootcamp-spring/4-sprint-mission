package com.sprint.mission.discodeit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {

    INVALID_TOKEN(401, "TOKEN_001", "토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN(401, "TOKEN_002", "토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(401, "TOKEN_003", "리프레시 토큰이 유효하지 않습니다"),
    INVALID_REFRESH_TOKEN_FORMAT(401, "TOKEN_003", "유효하지 않는 형식입니다."),
    EXPIRED_REFRESH_TOKEN(401, "TOKEN_004", "리프레시 토큰이 만료되었습니다."),
    TOKEN_CREATION_ERROR(500,"TOKEN_005", "토큰 생성 오류");


    private final int status;
    private final String code;
    private final String message;
}
