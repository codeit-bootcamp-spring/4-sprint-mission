package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository {
    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    Optional<Instant> findLastUpdatedAtByChannelId(UUID channelId);

    List<Message> findByChannelId(UUID channelId);

    boolean existsById(UUID id);

    void deleteByChannelId(UUID channelId);

    void deleteById(UUID id);
}
