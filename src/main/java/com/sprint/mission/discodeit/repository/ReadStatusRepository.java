package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {
    List<ReadStatus> findAll();

    void save(ReadStatus readStatus);

    ReadStatus findById(UUID id);

    void delete(UUID id);

    ReadStatus findByChannelIdAndUserId(UUID channelId, UUID userId);

    List<UUID> findByChannelId(UUID channelId);
}
