package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ReadStatusException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ReadStatusAlreadyExists extends ReadStatusException {

  public ReadStatusAlreadyExists(
      UUID userId, UUID channelId) {
    super(
        Instant.now(),
        ErrorCode.READSTATUS_WITH_USERID_AND_CHANNELID_EXISTS,
        Map.of(
            "userId", userId,
            "channelId", channelId
        ));
  }


}
