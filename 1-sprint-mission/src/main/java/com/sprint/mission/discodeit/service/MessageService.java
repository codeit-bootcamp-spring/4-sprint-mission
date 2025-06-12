package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Map;

public interface MessageService {
    Message sendMessage(User userId, Channel channelId, String content);
    List<Message> getMessages(User user, Channel channel);
    Message getMessageById(String messageId, User user, Channel channel);
    Message updateMessage(String messageId, String newContent);
    Message deleteMessage(String messageId);
    List<Message> deleteMessagesByChannelId(String channelId);
}
