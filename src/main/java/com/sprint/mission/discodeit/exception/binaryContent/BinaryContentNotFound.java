package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFound extends BinaryContentException {

  public BinaryContentNotFound(
      UUID binaryContentId) {
    super(
        Instant.now(),
        ErrorCode.BINARY_CONTENT_ALREADY_EXISTS,
        Map.of(
            "binaryContentId", binaryContentId
        ));
  }


}
