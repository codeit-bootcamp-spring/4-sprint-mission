package com.sprint.mission.discodeit.repository.fixture;

import com.sprint.mission.discodeit.entity.Channel;

public class ChannelFixture {
    public static Channel createPublicChannel() {
        return new Channel("public", "description");
    }

    public static Channel createPrivateChannel() {
        return new Channel();
    }
}
