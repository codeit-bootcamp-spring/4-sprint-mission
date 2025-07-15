package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class NoFindUserStatusException extends RuntimeException {

  @Getter
  private final String content;

  public NoFindUserStatusException(String message, String content) {
    super(message);
    this.content = content;
  }
}
