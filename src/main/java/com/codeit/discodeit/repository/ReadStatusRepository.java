package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    List<ReadStatus> loadReadStatuses();
    void saveReadStatuses(List<ReadStatus> readStatuses);
    void createReadStatus(ReadStatus readStatus);

    List<ReadStatus> findReadStatusesByUserId(UUID userId);
    List<ReadStatus> findReadStatusesByChannelId(UUID channelId);
    void deleteReadStatus(UUID userId, UUID channelId);
    void deleteReadStatusByChannelId(UUID channelId);
    Optional<ReadStatus> findReadStatusesByUserIdAndChannelId(UUID userId, UUID channelId);
    void updateReadStatus(ReadStatus readStatus);
    Optional<ReadStatus> findReadStatusesByReadStatusId(UUID readStatusId);

    void deleteReadStatusByReadStatusId(UUID readStatusId);
}
