package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.DTO.ReadStatusResponse;
import com.sprint.mission.discodeit.DTO.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);
    ReadStatusResponse find(UUID id);
    List<ReadStatusResponse> findAllByUserId(UUID userId);
    ReadStatusResponse update(ReadStatusUpdateRequest request);
    void delete(UUID id);
}
