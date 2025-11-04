package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT m FROM Message m "
      + "LEFT JOIN FETCH m.author a "
      + "LEFT JOIN FETCH a.profile "
      + "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt")
  Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
      @Param("createdAt") Instant createdAt,
      Pageable pageable);


  @Query("SELECT m.createdAt "
      + "FROM Message m "
      + "WHERE m.channel.id = :channelId "
      + "ORDER BY m.createdAt DESC LIMIT 1")
  Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

  void deleteAllByChannelId(UUID channelId);

  @Query("SELECT m FROM Message m "
      + "JOIN FETCH m.author a "
      + "JOIN FETCH m.channel c "
      + "JOIN ReadStatus rs ON m.channel = rs.channel "
      + "WHERE rs.user.username = :username "
      + "AND m.createdAt > rs.lastReadAt "
      + "ORDER BY m.createdAt DESC "
  )
  List<Message> findUnreadMessageForUser(@Param("username") String username);
}
