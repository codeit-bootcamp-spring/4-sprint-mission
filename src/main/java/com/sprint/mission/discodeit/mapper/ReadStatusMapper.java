package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

public class ReadStatusMapper {

    public ReadStatusMapper() {}

    public ReadStatus toEntity(ReadStatusCreateDto dto) {

        return new ReadStatus(
                dto.getUserId(),
                dto.getChannelId()
        );
    }

    public ReadStatusResponseDto toDto(ReadStatus readStatus) {

        return new ReadStatusResponseDto(
                readStatus.getId(),
                readStatus.getReadAt(),
                readStatus.getUserId(),
                readStatus.getChannelId()
        );
    }
}
