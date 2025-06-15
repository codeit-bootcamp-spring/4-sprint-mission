package com.sprint.mission.discodeit.service.utility;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public class MessageValidator {

    public static boolean detectMessageCreate(String contents, User user, Channel channel) {
        boolean detected = DetectUtility.detect(contents, user, channel);
        boolean matches = user.getChannels().contains(channel) && channel.getUsers().contains(user);
        return detected && matches;
    }

    public static boolean detectMessageUpdate(Message message, User user, String newContents) {
        boolean detected = DetectUtility.detect(message) && DetectUtility.detect(newContents, user);
        boolean matches = message.getUser().getId().equals(user.getId());
        if (detected && matches) {
            return true;
        } else {
            ErrorMessageUtility.printErrorMessage();
            return false;
        }
    }

    public static boolean detectMessageDelete(Message message, User user, Channel channel) {
        boolean detected = DetectUtility.detect(message) && DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean matches = message.getUser().getId().equals(user.getId()) && message.getChannel().getId().equals(channel.getId());
        if (detected && matches) {
            return true;
        } else {
            ErrorMessageUtility.printErrorMessage();
            return false;
        }
    }
}
