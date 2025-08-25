package com.codeit.discodeit.exception.readstatus;

import com.codeit.discodeit.exception.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReadStatusErrorCode implements ErrorCode {

  READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "읽기 상태를 찾을 수 없습니다.");
  private final int status;
  private final String message;

  ReadStatusErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  @Override
  public String getName() {
    return name();
  }
}
