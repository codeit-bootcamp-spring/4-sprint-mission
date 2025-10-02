package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNameAlreadyExistsException extends ChannelException {
    public ChannelNameAlreadyExistsException(String channelName) {
        super(ErrorCode.DUPLICATE_CHANNEL);
        withChannelName(channelName);
    }

}
