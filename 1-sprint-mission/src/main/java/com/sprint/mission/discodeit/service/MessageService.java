package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface MessageService {
    Message sendMessage(User userId, Channel channelId, String content);
    List<Message> getMessages();
    Message getMessageById(String messageId);
    Message updateMessage(String messageId, String newContent);
    Message deleteMessage(String messageId);
}
