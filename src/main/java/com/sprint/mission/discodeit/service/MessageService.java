package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    public void addMessage(Message message);
    public List<Message> getAllMessages();
    public void deleteMessage(UUID messageId);
    public void updateMessage(UUID messageId, String replacedContent);
    public void replaceSubstringInContent(UUID messageId, String originalContent, String replacedContent);
    public List<Message> findMessagesByChannelName(String channelName);
    public List<Message> findMessagesBySender(User sender);
    public List<Message> findMessagesByKeyword(String keyword);
}
