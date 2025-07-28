package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findAllByChannelId(UUID channelId);

  Slice<Message> findByChannelId(UUID channelId, Pageable pageable);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);
}
