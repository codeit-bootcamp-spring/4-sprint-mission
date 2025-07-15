package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class NoFindBinaryContent extends RuntimeException {

  @Getter
  private final String content;

  public NoFindBinaryContent(String message, String content) {
    super(message);
    this.content = content;
  }
}
