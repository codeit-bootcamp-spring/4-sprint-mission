package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.UUID;

public interface MessageService {
    public Message createMessage(Message message);
    public void findAllMessages();
    public Message findMessageById(UUID id);
    public void updateMessage(UUID id, String updateMessage);
    public void deleteMessage(Message message);
}
