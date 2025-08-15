package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ChannelUpdatePrivateChannelException extends ChannelException {

  public ChannelUpdatePrivateChannelException(
      UUID channelId) {
    super(
        Instant.now(),
        ErrorCode.CANNOT_UPDATE_PRIVATE_CHANNEL,
        Map.of("channelId", channelId));
  }

}
