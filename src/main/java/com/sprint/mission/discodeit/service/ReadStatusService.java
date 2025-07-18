package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    com.sprint.mission.discodeit.entity.ReadStatus create(ReadStatusCreateRequest request);
    com.sprint.mission.discodeit.entity.ReadStatus find(UUID readStatusId);
    List<com.sprint.mission.discodeit.entity.ReadStatus> findAllByUserId(UUID userId);
    com.sprint.mission.discodeit.entity.ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request);
    void delete(UUID readStatusId);
    ReadStatus makeDto(com.sprint.mission.discodeit.entity.ReadStatus readStatus);
}
