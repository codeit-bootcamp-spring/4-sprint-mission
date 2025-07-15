package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class NoFindReadStatusException extends RuntimeException {

  @Getter
  private final String content;

  public NoFindReadStatusException(String message, String content) {
    super(message);
    this.content = content;
  }
}
