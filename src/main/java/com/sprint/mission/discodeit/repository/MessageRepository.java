package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message sendMessage(Path dir, Message message);

    List<Message> loadAllMessages(Path dir);

    Optional<Message> loadMessageById(Path dir, UUID id);

    void deleteMessage(Path dir, Message message);
}
