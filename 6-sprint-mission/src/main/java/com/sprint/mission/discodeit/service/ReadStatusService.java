package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    public ReadStatusDto createReadStatus(ReadStatusCreateRequest readStatusCreateRequest);

    public ReadStatusDto findReadStatus(UUID readStatusId);

    public List<ReadStatusDto> findAllReadStatusByUserId(UUID userId);

    public List<ReadStatusDto> findAllReadStatusByChannelId(UUID channelId);

    public ReadStatusDto updateReadStatus(
            UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest);

    public void deleteReadStatus(UUID readStatusId);
}
