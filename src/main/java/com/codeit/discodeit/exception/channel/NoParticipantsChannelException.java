package com.codeit.discodeit.exception.channel;

import java.time.Instant;
import java.util.Map;

public class NoParticipantsChannelException extends ChannelException {
  public NoParticipantsChannelException(Map<String, Object> details) {
    super(Instant.now(), ChannelErrorCode.No_Participants_Channel, details);
  }
}
