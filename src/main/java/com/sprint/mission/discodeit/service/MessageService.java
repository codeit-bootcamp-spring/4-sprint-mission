package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public interface MessageService {

    Message createMessage(User user, Channel channel, String contents);
    void deleteMessage(User user, Message message);
    void updateMessage(User user, Message message, String newContents);

    void printMessagesByChannel(Channel channel);
    void printMessage(Message message);
    void printAllMessage();
    void printAllMessageByUser(User user);
}
