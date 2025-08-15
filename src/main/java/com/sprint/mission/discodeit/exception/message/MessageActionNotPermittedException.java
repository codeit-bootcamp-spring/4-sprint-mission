package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.MessageException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MessageActionNotPermittedException extends MessageException {

  public MessageActionNotPermittedException(
      UUID messageId) {
    super(
        Instant.now(),
        ErrorCode.MESSAGE_ACTION_NOT_PERMITTED,
        Map.of("messageId", messageId));
  }
}
