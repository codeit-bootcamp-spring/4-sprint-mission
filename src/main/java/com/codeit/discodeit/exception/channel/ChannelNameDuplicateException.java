package com.codeit.discodeit.exception.channel;

import java.time.Instant;
import java.util.Map;

public class ChannelNameDuplicateException extends ChannelException {
  public ChannelNameDuplicateException(Map<String, Object> details) {
    super(Instant.now(), ChannelErrorCode.CHANNEL_NAME_DUPLICATE, details);
  }
}
