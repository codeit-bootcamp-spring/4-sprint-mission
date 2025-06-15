package com.sprint.mission.discodeit.service.utility;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public class ChannelValidator {

    public static boolean detectChannelDelete(Channel channel, User hostUser) {
        boolean detected = DetectUtility.detect(channel) && DetectUtility.detect(hostUser);
        boolean matches = channel.getHostUserId().equals(hostUser.getId());
        return detected && matches;
    }
}
