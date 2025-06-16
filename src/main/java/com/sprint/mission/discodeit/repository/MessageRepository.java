package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;

public interface MessageRepository {
    Message createMessage(User user, Channel channel, String contents);
    void deleteMessage(User user, Message message);
    void updateMessage(User user, Message message, String newContents);

    List<Message> getMessages();
}
