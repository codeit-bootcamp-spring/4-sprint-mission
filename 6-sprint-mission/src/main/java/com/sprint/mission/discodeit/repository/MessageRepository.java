package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    public List<Message> findAllByChannelId(UUID channelId);

    public Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);

    public List<Message> findAllByChannelIdAndAuthorId(UUID channelId, UUID userId);

    Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);
}
