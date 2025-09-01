package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BytesEmptyException extends BinaryContentException {
  public BytesEmptyException(byte[] bytes) {
    super(ErrorCode.BINARY_BYTES_EMPTY,
            ErrorCode.BINARY_BYTES_EMPTY.getMessage(),
            Map.of("size", bytes == null ? null : bytes.length));
  }
}
