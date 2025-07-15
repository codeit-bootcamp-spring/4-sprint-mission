package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class DuplicateUserException extends RuntimeException {

  @Getter
  private final String content;

  public DuplicateUserException(String message, String content) {
    super(message);
    this.content = content;
  }
}
