package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReadStatusMapper {

    public ReadStatus readStatusReqestDtoToReadStatus(ReadStatusRequestDto dto) {
        return new ReadStatus(dto.userId(), dto.channelId());
    }

    public ReadStatus readStatusCreateDtoToReadStatus(ReadStatusCreateDto dto) {
        return new ReadStatus(dto.userId(), dto.channelId());
    }

    public ReadStatusResponseDto toReadStatusResponse(ReadStatus readStatus) {
        return new ReadStatusResponseDto(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt());
    }
}
