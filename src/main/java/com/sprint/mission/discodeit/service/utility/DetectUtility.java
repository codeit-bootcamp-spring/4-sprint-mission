package com.sprint.mission.discodeit.service.utility;

import com.sprint.mission.discodeit.domain.ChannelType;
import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public class DetectUtility {

    public static boolean detect(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean detect(User user) {
        return user != null && user.getStatus() == UserType.UserStatus.ACTIVE;
    }

    public static boolean detect(Channel channel) {
        return channel != null && channel.getChannelStatus() == ChannelType.ChannelStatus.ACTIVE;
    }

    public static boolean detect(Message message) {
        return message != null;
    }

    public static boolean detect(String str, User user) {
        return detect(str) && detect(user);
    }

    public static boolean detect(String str, User user, Channel channel) {
        return detect(str, user) && detect(channel);
    }
}
