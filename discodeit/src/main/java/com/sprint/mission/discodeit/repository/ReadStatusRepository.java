package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    boolean existsById(UUID userId, UUID channelId);
    void deleteByChannelIdAndUserId(UUID channelId, UUID userId);
    void deleteByUserId(UUID userId);
    void deleteByChannelId(UUID channelId);
}
