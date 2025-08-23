package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ChannelPrivateUpdateNotAllowedException extends ChannelException {
    public ChannelPrivateUpdateNotAllowedException(Map<String, Object> details) {
        super(ErrorCode.CHANNEL_PRIVATE_UPDATE_NOT_ALLOWED, details);
    }
}
