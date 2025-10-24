package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest);

    List<ReadStatus> findAllByUserId(UUID userId);

    ReadStatus update(UUID id, ReadStatusUpdateRequest readStatusUpdateRequest);

    void delete(UUID id);

    List<ReadStatus> findAllByMessage(Message message);
}
