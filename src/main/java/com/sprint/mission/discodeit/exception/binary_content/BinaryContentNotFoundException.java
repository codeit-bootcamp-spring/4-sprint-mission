package com.sprint.mission.discodeit.exception.binary_content;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
