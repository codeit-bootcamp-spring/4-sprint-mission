package com.sprint.mission.discodeit.exception;

public class MessageNotFoundException extends RuntimeException {

  public MessageNotFoundException() {
    super();
  }

  public MessageNotFoundException(String message) {
    super(message);
  }
}
