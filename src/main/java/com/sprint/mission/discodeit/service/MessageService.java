package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    // Create
    public Optional<Message> addMessage(User user, Channel channel, String contents);

    // Read
    public List<Message> getMessages();

    public Optional<Message> getMessageById(UUID id);

    // Update
    public void updateMessageContentsById(UUID id, User user, String newContents);

    // Delete
    public void deleteMessageById(UUID id, User user, Channel channel);
}
