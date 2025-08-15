package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UserNotInChannelException extends ChannelException {

  public UserNotInChannelException(
      UUID channelId) {
    super(
        Instant.now(),
        ErrorCode.USER_NOT_IN_CHANNEL,
        Map.of("channelId", channelId));
  }
}
