package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UserAlreadyInChannelException extends ChannelException {

  public UserAlreadyInChannelException(
      UUID channelId) {
    super(
        Instant.now(),
        ErrorCode.USER_ALREADY_IN_CHANNEL,
        Map.of("channelId", channelId));
  }
}
