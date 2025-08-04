package com.sprint.mission.discodeit.maper;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

    public ReadStatus readStatusCreateRequestToReadStatus(ReadStatusCreateRequest dto) {
        return new ReadStatus(dto.userId(),dto.channelId(),dto.lastReadAt());
    }

    public ReadStatusResponseDto readStatusToReadStatusResponseDto(ReadStatus readStatus) {
        return new ReadStatusResponseDto(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()

        );
    }
}
