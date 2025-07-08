package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto createReadStatus(ReadStatusRequest readStatusRequest);
    List<ReadStatusDto> findReadStatus(UUID readStatusId);
    List<ReadStatusDto> finaAllReadStatus();
    ReadStatusDto updateReadStatus(UUID id, ReadStatusRequest readStatusRequest);
    void deleteReadStatus(UUID id);
}
