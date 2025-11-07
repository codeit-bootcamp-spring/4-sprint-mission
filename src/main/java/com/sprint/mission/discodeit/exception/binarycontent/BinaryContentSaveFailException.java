package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class BinaryContentSaveFailException extends BinaryContentException {
    public BinaryContentSaveFailException() {
        super(ErrorCode.BINARY_CONTENT_SAVE_FAIL);
    }

    public static BinaryContentSaveFailException withId(UUID binaryContentId) {
        BinaryContentSaveFailException exception = new BinaryContentSaveFailException();
        exception.addDetail("binaryContentId", binaryContentId);
        return exception;
    }
}
