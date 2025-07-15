package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class NoFindMessageException extends RuntimeException {

  @Getter
  private final String content;

  public NoFindMessageException(String message, String content) {
    super(message);
    this.content = content;
  }
}
