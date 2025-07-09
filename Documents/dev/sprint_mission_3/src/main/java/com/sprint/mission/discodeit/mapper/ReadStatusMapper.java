package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {
    public ReadStatus readStatusCreateDtoToReadStatus(ReadStatusCreateDto dto) {
        return new ReadStatus(
                dto.getUserId(),
                dto.getChannelId(),
                dto.getReadAt()
        );
    }

    public ReadStatusResponseDto readStatusToReadStatusResponseDto(ReadStatus readStatus) {
        return new ReadStatusResponseDto(
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getReadAt(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt()
        );
    }
}
