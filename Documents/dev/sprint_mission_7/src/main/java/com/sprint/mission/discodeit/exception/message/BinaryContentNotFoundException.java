package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class BinaryContentNotFoundException extends MessageException {
    public BinaryContentNotFoundException(UUID attachmentId) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
        withAttachmentId(attachmentId);
    }
}
