package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDto create(ReadStatusCreateDto readStatusCreateDto);
    ReadStatusResponseDto findById(UUID readStatusId);
    List<ReadStatusResponseDto> findAllByUserId(UUID userId);

    ReadStatusResponseDto update(ReadStatusUpdateDto readStatusUpdateDto);
    void delete(UUID readStatusId);
}
