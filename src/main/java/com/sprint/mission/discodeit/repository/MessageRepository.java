package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    // Create
    public void addMessageAndSave(Message message, User user, Channel channel);

    // Read
    public List<Message> getAll();

    public Message getById(UUID id);

    // Update
    public void updateContents(Message message, User user, String newContents);

    // Delete
    public void delete(Message message, User user, Channel channel);

    public void deleteAll();
}
