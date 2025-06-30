package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    public Message createMessage(String message);
    public Message searchMessage(UUID id);
    public List<Message> searchAll();
    public Message updateMessage(UUID id, String message);
    public void deleteMessage(UUID id);
}
