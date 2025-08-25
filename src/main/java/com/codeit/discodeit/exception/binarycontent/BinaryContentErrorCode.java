package com.codeit.discodeit.exception.binarycontent;

import com.codeit.discodeit.exception.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BinaryContentErrorCode implements ErrorCode {

  BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "바이너리 컨텐츠를 찾을 수 없습니다.");
  private final int status;
  private final String message;

  BinaryContentErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  @Override
  public String getName() {
    return name();
  }
}
