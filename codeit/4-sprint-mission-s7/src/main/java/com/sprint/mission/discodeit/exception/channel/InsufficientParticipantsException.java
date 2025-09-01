package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InsufficientParticipantsException extends ChannelException {

    public InsufficientParticipantsException(String message) {
      super(ErrorCode.INSUFFICIENT_PARTICIPANTS, message);
    }
}
