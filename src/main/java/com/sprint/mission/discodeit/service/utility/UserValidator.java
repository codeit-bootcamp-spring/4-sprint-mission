package com.sprint.mission.discodeit.service.utility;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public class UserValidator {

    public static boolean detectJoinChannel(User user, Channel channel) {
        boolean detected = DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean notJoined = !user.getChannels().contains(channel);
        return detected && notJoined;
    }

    public static boolean detectOutChannel(User user, Channel channel) {
        boolean detected = DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean joined = user.getChannels().contains(channel);
        return detected && joined;
    }
}
