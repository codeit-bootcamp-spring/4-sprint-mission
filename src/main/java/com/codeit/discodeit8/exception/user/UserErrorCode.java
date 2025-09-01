package com.codeit.discodeit8.exception.user;

import com.codeit.discodeit8.exception.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {

  USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "유저를 찾을 수 없습니다."),
  USER_NAME_EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST.value(), "유저 이름 또는 이메일이 중복 입니다."),
  USER_WRONG_PASSWORD(HttpStatus.BAD_REQUEST.value(), "비밀번호가 잘못 되었습니다."),
  USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 User의 UserStatus를 찾을 수 없음");

  private final int status;
  private final String message;

  UserErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  @Override
  public String getName() {
    return name();
  }
}
