package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    public ReadStatusResponseDto createReadStatus(ReadStatusCreateDto readStatusCreateDto);

    public ReadStatusResponseDto findReadStatus(UUID readStatusId);

    public List<ReadStatusResponseDto> findAllReadStatus(UUID userId);

    public ReadStatusResponseDto updateReadStatus(ReadStatusUpdateDto readStatusUpdateDto);

    public void deleteReadStatus(UUID readStatusId);
}
