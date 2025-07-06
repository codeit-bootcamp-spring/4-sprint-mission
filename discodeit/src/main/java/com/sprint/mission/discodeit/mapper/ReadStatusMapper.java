package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {
    public static ReadStatusResponseDto entityToDto(ReadStatus readStatus) {
        return new ReadStatusResponseDto(
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getReadTime()
        );
    }
}
