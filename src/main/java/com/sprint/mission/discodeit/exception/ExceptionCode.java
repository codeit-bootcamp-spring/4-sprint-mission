package com.sprint.mission.discodeit.exception;

import lombok.Getter;

public enum ExceptionCode {
    USER_NOT_FOUND(404, "User Not Found"),
    EMAIL_OR_USERNAME_ALREADY_EXISTS(400, "User With Email Already Exists"),
    CHANNEL_OR_USER_NOT_FOUND(404, "Channel | User With ID Not Found"),
    READSTATUS_ALREADY_EXISTS(400, "ReadStatus With UserId and ChannelId Already Exists"),
    WRONG_PASSWORD(400, "Wrong Password"),
    USERSTATUS_NOT_FOUND(404, "UserStatus Not Found"),
    READSTATUS_NOT_FOUND(404, "Message ReadStatus Not Found"),
    MESSAGE_NOT_FOUND(404, "Message Not Found"),
    CHANNEL_NOT_FOUND(404, "Channel Not Found"),
    PRIVATE_CHANNEL_CANNOT_UPDATE(400, "Private Channel Cannot Be Updated"),
    BINARYCONTENT_NOT_FOUND(404, "BinaryContent Not Found"),

    BINARYCONTENT_EXISTS(400, "BinaryContent Exists");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
