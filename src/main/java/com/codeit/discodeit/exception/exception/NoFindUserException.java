package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class NoFindUserException extends RuntimeException {

  @Getter
  private final String content;

  public NoFindUserException(String message, String content) {
    super(message);
    this.content = content;
  }
}
