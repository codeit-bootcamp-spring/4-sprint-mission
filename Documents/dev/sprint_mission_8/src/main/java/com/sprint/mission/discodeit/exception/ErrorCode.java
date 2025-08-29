package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum ErrorCode {
    USER_NOTFOUND("해당 사용자를 찾을 수 없습니다.", 404),
    DUPLICATE_USER("이미 존재하는 유저명입니다.",409),
    DUPLICATE_EMAIL("이미 가입된 이메일입니다.",409),
    CHANNEL_NOT_FOUND("해당 채널을 찾을 수 없습니다.",404),
    DUPLICATE_CHANNEL("이미 존재하는 채널명입니다.",409),
    PRIVATE_CHANNEL_UPDATE("비공개 채널은 수정할 수 없습니다.",400),
    MESSAGE_NOT_FOUND("해당 메세지를 찾을 수 없습니다.",404),
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다.",401),
    BINARY_CONTENT_NOT_FOUND("첨부 파일을 찾을 수 없습니다.",404);

    private final String message;
    private final int status;

    ErrorCode (String message, int status) {
        this.message = message;
        this.status = status;
    }

    public ErrorCode getErrorCode () {
        return this;
    }

    public String getMessage () {
        return message;
    }
}
