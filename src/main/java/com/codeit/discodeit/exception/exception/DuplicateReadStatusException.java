package com.codeit.discodeit.exception.exception;

import lombok.Getter;

public class DuplicateReadStatusException extends RuntimeException {

  @Getter
  private final String content;

  public DuplicateReadStatusException(String message, String content) {
    super(message);
    this.content = content;
  }
}
