package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  // The 'message' parameter is passed to the parent 'RuntimeException'
  public DiscodeitException(String message, Instant timestamp, ErrorCode errorCode,
      Map<String, Object> details) {
    super(message); // this is the fix
    this.timestamp = timestamp;
    this.errorCode = errorCode;
    this.details = details != null ? Map.copyOf(details) : Map.of();
  }
}

