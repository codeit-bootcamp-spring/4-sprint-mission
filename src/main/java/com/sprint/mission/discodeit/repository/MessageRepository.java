package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAll();

    @EntityGraph(attributePaths = {"attachments", "author"})
    Optional<Message> findById(UUID id);

    void delete(Message message);

    Message save(Message message);

    @EntityGraph(attributePaths = {"attachments", "author"})
    Page<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);

    Optional<Message> findTopByChannelOrderByUpdatedAtDesc(Channel channel);

    Page<Message> findAllByOrderByIdDescCreatedAtDesc(Pageable pageable);

    Page<Message> findByIdLessThanOrderByIdDescCreatedAtDesc(Instant cursor, Pageable pageable);
}
