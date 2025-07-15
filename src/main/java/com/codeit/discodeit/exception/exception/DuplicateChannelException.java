package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class DuplicateChannelException extends RuntimeException {

  @Getter
  private final String content;

  public DuplicateChannelException(String message, String content) {
    super(message);
    this.content = content;
  }
}
