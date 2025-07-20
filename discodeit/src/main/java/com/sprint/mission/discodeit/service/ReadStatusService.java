package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto.*;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusRequest dto);
    ReadStatusResponse find(UUID readStatusId);
    List<ReadStatusResponse> findAllByUserId(UUID userId);
    ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest dto);
    void delete(UUID readStatusId);
}
