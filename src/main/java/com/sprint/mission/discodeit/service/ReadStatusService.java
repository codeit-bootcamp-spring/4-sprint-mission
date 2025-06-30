package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponseDto create(ReadStatusCreateDto readStatusCreateDto);
    ReadStatusResponseDto find(UUID id);
    List<ReadStatusResponseDto> findAllByUserId(UUID userId);
    ReadStatusResponseDto update(ReadStatusUpdateDto readStatusUpdateDto);
    void delete(UUID id);
}
