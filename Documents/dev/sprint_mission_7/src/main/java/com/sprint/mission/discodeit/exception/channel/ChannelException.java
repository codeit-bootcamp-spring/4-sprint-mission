package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class ChannelException extends DiscodeitException {

    protected ChannelException(ErrorCode errorCode) {super(errorCode);}

    public ChannelException withChannelId(UUID channelId) {
        setDetail("channelId", channelId);
        return this;
    }

    public ChannelException withChannelName(String channelName) {
        setDetail("channelName", channelName);
        return this;
    }
}
