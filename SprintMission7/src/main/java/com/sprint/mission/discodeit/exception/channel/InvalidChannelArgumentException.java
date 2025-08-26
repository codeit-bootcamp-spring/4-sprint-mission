package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class InvalidChannelArgumentException extends ChannelException {

  public InvalidChannelArgumentException(Map<String, Object> details) {
    super(Instant.now(), ErrorCode.INVALID_CHANNEL_ARGUMENT, details);
  }
}
