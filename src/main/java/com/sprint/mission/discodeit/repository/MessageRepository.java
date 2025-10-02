package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Modifying
    @Query("DELETE FROM Message m WHERE m.channel.id = :channelId")
    void deleteAllByChannelId(UUID channelId);

    @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId")
    List<Message> findAllByChannelId(UUID channelId);

    @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId AND m.createdAt < :cursor ORDER BY m.createdAt DESC")
    Slice<Message> findChannelMessagesByCursor(UUID channelId, Instant cursor, Pageable pageable);
}
