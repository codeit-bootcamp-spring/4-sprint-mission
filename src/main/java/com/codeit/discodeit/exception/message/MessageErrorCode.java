package com.codeit.discodeit.exception.message;

import com.codeit.discodeit.exception.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageErrorCode implements ErrorCode {

  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "메세지를 찾을 수 없습니다.");
  private final int status;
  private final String message;

  MessageErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  @Override
  public String getName() {
    return name();
  }
}
