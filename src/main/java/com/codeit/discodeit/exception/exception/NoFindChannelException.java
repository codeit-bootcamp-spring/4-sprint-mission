package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class NoFindChannelException extends RuntimeException {

  @Getter
  private final String content;

  public NoFindChannelException(String message, String content) {
    super(message);
    this.content = content;
  }
}
