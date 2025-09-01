package com.codeit.discodeit8.exception.message;

import java.time.Instant;
import java.util.Map;

public class MessageNotFoundException extends MessageException {

  public MessageNotFoundException(Map<String, Object> details) {
    super(Instant.now(), MessageErrorCode.MESSAGE_NOT_FOUND, details);
  }
}
