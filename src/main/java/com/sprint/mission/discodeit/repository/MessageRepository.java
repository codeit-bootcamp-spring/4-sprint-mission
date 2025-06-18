package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Optional<Message> findById(UUID messageId);
    void save(Message message);
    void deleteById(UUID messageId);
    List<Message> findAll();
}
