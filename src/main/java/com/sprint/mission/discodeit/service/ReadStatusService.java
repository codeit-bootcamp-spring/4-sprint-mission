package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatusDto.create request);

    ReadStatus find(UUID readStatusId);

    List<ReadStatus> findAllByUserId(UUID userId);

    List<ReadStatus> findAllByChannelId(UUID channelId);

    ReadStatus update(UUID readStatusId);

    List<ReadStatus> updateByChannelId(UUID channelId);

    void delete(UUID readStatusId);
}
