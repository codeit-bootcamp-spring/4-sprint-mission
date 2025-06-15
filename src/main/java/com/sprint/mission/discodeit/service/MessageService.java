package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // Create
    public Message createMessage(String contents, User user, Channel channel);

    // Read
    public List<Message> getMessages();

    public Message getMessageById(UUID id);

    // Update
    public void updateMessageContentsByMessage(Message message, User user, String newContents);

    // Delete
    public void deleteMessageByMessage(Message message, User user, Channel channel);

    public void deleteAllMessages();

}
