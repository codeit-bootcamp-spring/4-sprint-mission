package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

public class ChannelFixture {
    public static Channel createPublicChannel() {
        return new Channel(ChannelType.PUBLIC,"공개방","모든 유저가 참여가능");
    }

    public static Channel createPrivateChannel() {
        return new Channel(ChannelType.PRIVATE,null,null);
    }
}
