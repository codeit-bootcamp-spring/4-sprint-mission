package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDto create(ReadStatusCreateRequest request);
    ReadStatusResponseDto find(UUID readStatusId);
    List<ReadStatusResponseDto> findAllByUserId(UUID userId);
    ReadStatusResponseDto update(UUID readStatusId, ReadStatusUpdateRequest request);
    void delete(UUID readStatusId);
}
