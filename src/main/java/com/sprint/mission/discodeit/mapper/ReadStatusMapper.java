package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

    public ReadStatus ReadStatusCreateDtoToReadStatus(ReadStatusCreateDto dto) {
        return new ReadStatus(dto.getUserId(), dto.getChannelId());
    }

    public ReadStatusResponseDto ReadStatusToReadStatusResponseDto(ReadStatus readStatus) {
        return new ReadStatusResponseDto(readStatus.getId(),readStatus.getUserId(),readStatus.getChannelId(),readStatus.getLastReadAt());
    }
}
