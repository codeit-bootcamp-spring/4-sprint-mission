package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class PrivateChannelUpdateNotAllowedException extends RuntimeException {

  @Getter
  private final String content;

  public PrivateChannelUpdateNotAllowedException(String message, String content) {
    super(message);
    this.content = content;
  }
}
