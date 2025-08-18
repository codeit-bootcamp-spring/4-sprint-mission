package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ContentTypeBlankException extends BinaryContentException {
    public ContentTypeBlankException(String contentType) {
        super(ErrorCode.BINARY_CONTENT_TYPE_BLANK,
                ErrorCode.BINARY_CONTENT_TYPE_BLANK.getMessage(),
                Map.of("contentType", contentType));
    }
}
