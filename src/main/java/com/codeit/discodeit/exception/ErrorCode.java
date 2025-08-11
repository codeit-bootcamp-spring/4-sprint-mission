package com.codeit.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  USER_NOT_FOUND("사용자를 찾을 수 없음"),
  WRONG_PASSWORD("비밀번호가 일치하지 않음"),
  NO_FIND_BINARY_CONTENT("첨부 파일을 찾을 수 없음"),
  NO_FIND_CHANNEL("채널을 찾을 수 없음"),
  DUPLICATE_CHANNEL("채널 이름이 중복"),
  NO_FIND_MESSAGE("메세지를 찾을 수 없음"),
  NO_FIND_USER("유저를 찾을 수 없음"),
  NO_FIND_READ_STATUS("읽음 상태를 찾을 수 없음"),
  DUPLICATE_USER("유저 이름 또는 이메일이 중복됨"),
  NO_FIND_USER_STATUS("유저 상태를 찾을 수 없음");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}