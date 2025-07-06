package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDto create(ReadStatusCreateDto dto);
    ReadStatusResponseDto find(UUID channelId, UUID userId);
    List<ReadStatusResponseDto> findAllByUserId(UUID userId);
    ReadStatusResponseDto update(ReadStatusDto dto);
    void delete(UUID channelId, UUID userId);
}
