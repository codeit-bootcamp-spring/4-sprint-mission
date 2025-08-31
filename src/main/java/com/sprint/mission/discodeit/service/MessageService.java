package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    public Message createMessage(String content, User user, Channel channel);
    public List<Message> getMessages();
    public Optional<Message> getMessagesById(UUID messageId);
    public void updateMessage(UUID messageId, String content);;
    public void removeMessage(Message messgae);
}
