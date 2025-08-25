package com.codeit.discodeit.exception.binarycontentstorage;

import com.codeit.discodeit.exception.global.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BinaryContentStorageErrorCode implements ErrorCode {

  BINARY_CONTENT_STORAGE_FAIL_SAVE_FILE(HttpStatus.BAD_REQUEST.value(), "바이너리 컨텐츠 저장에 실패했습니다."),
  BINARY_CONTENT_STORAGE_FAIL_READ_FILE(HttpStatus.BAD_REQUEST.value(), "바이너리 컨텐츠 읽기에 실패했습니다."),
  BINARY_CONTENT_STORAGE_FAIL_DOWNLOAD_FILE(HttpStatus.BAD_REQUEST.value(), "바이너리 컨텐츠 다운로드에 실패했습니다.");


  private final int status;
  private final String message;

  BinaryContentStorageErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  @Override
  public String getName() {
    return name();
  }
}
