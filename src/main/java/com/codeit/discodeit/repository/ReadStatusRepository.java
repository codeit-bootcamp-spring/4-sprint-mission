package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    void createReadStatus(ReadStatus readStatus);
    void updateReadStatus(ReadStatus readStatus);

    Optional<ReadStatus> findReadStatusesByReadStatusId(UUID readStatusId);
    Optional<ReadStatus> findReadStatusesByUserIdAndChannelId(UUID userId, UUID channelId);

    List<ReadStatus> findReadStatusesByUserId(UUID userId);
    List<ReadStatus> findReadStatusByChannel(Channel channel);
}
