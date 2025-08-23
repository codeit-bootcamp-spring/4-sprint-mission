package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST.value(), "E001", "유효성 검사 실패"),

    // Auth
    AUTH_FAILED(HttpStatus.UNAUTHORIZED.value(), "A002", "Authentication Failed"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "U001", "사용자를 찾을 수 없습니다."),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "U002", "이미 존재하는 이메일입니다."),
    USER_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "U003", "이미 존재하는 이름입니다."),

    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C001", "채널을 찾을 수 없습니다."),
    CHANNEL_PRIVATE_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN.value(), "C002", "private 채널은 수정할 수 없습니다."),

    // Message
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "M001", "메시지를 찾을 수 없습니다."),

    // Binary Content
    BINARYCONTENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "BC002", "<UNK> <UNK> <UNK> <UNK>."),

    // Read Status
    READSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "RS001", "ReadStatus with id not found."),

    // User Status
    USERSTATUS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "US001", "UserStatus with id not found."),
    USERSTATUS_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "US002", "UserStatus with userId already exists.");

    private final int status;
    private final String code;
    private final String message;
}
