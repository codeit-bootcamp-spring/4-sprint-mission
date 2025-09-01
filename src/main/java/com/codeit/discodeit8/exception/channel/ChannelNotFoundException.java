package com.codeit.discodeit8.exception.channel;

import java.time.Instant;
import java.util.Map;

public class ChannelNotFoundException extends ChannelException {

  public ChannelNotFoundException(Map<String, Object> details) {
    super(Instant.now(), ChannelErrorCode.CHANNEL_NOT_FOUND, details);
  }
}
