package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.ReadStatusDto.*;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {
    public ReadStatusResponse entityToReadStatusResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt()
        );
    }
}
