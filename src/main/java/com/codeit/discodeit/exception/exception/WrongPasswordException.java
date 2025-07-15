package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class WrongPasswordException extends RuntimeException {

  @Getter
  private final String content;

  public WrongPasswordException(String message, String content) {

    super(message);
    this.content = content;
  }
}
