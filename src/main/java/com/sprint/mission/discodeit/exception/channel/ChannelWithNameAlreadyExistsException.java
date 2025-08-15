package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ChannelWithNameAlreadyExistsException extends ChannelException {

  public ChannelWithNameAlreadyExistsException(
      String channelName) {
    super(
        Instant.now(),
        ErrorCode.CHANNEL_WITH_NAME_ALREADY_EXISTS,
        Map.of("channelName", channelName));
  }

}
