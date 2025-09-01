package com.codeit.discodeit8.repository;

import com.codeit.discodeit8.entity.Channel;
import com.codeit.discodeit8.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

  List<ReadStatus> findAllByUserId(UUID userId);

  List<ReadStatus> findByChannel(Channel channel);
}
