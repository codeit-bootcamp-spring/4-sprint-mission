package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public class FileAccessException extends RuntimeException {

  private String message;
  private int status;

  public FileAccessException(String message, int status) {
    super(message);
    this.status = status;
  }
}
