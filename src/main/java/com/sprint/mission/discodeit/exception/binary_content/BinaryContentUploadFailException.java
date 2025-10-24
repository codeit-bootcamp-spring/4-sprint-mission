package com.sprint.mission.discodeit.exception.binary_content;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BinaryContentUploadFailException extends BinaryContentException {
    public BinaryContentUploadFailException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
