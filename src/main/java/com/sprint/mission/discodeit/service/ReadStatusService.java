package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatusResponseDto create(ReadStatusCreateDto readStatusCreateDto);

    ReadStatusResponseDto find(UUID id);

    List<ReadStatusResponseDto> findAllByUserId(UUID userId);

    ReadStatus update(ReadStatusUpdateDto dto);

    void delete(UUID id);
}
