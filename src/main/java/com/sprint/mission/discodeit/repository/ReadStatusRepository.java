package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  // 페이징
  List<ReadStatus> findAllByUserId(UUID userId);

  // 페이징
  List<ReadStatus> findAllByChannelId(UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}
