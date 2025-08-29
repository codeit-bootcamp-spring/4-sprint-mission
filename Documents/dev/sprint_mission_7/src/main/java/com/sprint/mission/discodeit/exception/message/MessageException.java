package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class MessageException extends DiscodeitException {
    protected MessageException(ErrorCode errorCode) {super(errorCode);}

    public MessageException withMessageId(UUID messageId) {
        setDetail("messageId", messageId);
        return this;
    }

    public MessageException withAttachmentId(UUID attachmentId) {
        setDetail("attachmentId", attachmentId);
        return this;
    }
}
