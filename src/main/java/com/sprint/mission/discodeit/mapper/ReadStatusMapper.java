package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReadStatusMapper {
    public ReadStatus toReadStatus(ReadStatusCreateRequest readStatusCreateRequest) {
        return new ReadStatus(readStatusCreateRequest.userId(), readStatusCreateRequest.channelId(), Instant.now());
    }

    public ReadStatusResponseDto toReadStatusResponseDto(ReadStatus readStatus) {
        return new ReadStatusResponseDto(readStatus.getId(), readStatus.getUserId(), readStatus.getChannelId(), readStatus.getLastReadAt());
    }

    public ReadStatusCreateDto ofReadStatusCreateDto(UUID channelId, ReadStatusCreateRequest readStatusCreateRequest) {
        return new ReadStatusCreateDto(channelId, readStatusCreateRequest.userId());
    }
}
