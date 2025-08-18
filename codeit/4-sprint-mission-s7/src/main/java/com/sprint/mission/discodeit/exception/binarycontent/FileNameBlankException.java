package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class FileNameBlankException extends BinaryContentException {
  public FileNameBlankException(String fileName) {
    super(ErrorCode.BINARY_FILENAME_BLANK,
            ErrorCode.BINARY_FILENAME_BLANK.getMessage(),
            Map.of("fileName", fileName));
  }

}
