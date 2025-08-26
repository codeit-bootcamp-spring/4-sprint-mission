package com.sprint.mission.discodeit.exception;

public enum ErrorCode {
  USER_NOT_FOUND("User not found"),
  DUPLICATE_USER("Duplicate user"),
  INVALID_USER_ARGUMENT("Invalid user argument"),

  CHANNEL_NOT_FOUND("Channel not found"),
  INVALID_CHANNEL_ARGUMENT("Invalid channel argument"),
  PRIVATE_CHANNEL_UPDATE("Private channel update not allowed"),

  MESSAGE_NOT_FOUND("Message not found"),
  DUPLICATE_MESSAGE("Duplicate message"),

  BINARY_CONTENT_NOT_FOUND("Binary content not found"),
  DUPLICATE_BINARY_CONTENT("Duplicate binary content"),

  INVALID_AUTH_ARGUMENT("Invalid auth argument"),

  READ_STATUS_NOT_FOUND("Read status not found"),
  DUPLICATE_READ_STATUS("Duplicate read status"),

  USER_STATUS_NOT_FOUND("User status not found"),
  DUPLICATE_USER_STATUS("Duplicate user status"),

  VALIDATION_FAILED("Validation failed"),
  ;

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
