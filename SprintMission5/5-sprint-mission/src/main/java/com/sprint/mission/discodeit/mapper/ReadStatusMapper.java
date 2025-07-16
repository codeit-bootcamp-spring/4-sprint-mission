package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatus;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReadStatusMapper {

  public ReadStatusEntity readStatusToReadStatus(ReadStatus dto) {
    return new ReadStatusEntity(dto.userId(), dto.channelId());
  }

  public ReadStatusEntity readStatusCreateDtoToReadStatus(ReadStatusCreateRequest dto) {
    return new ReadStatusEntity(dto.userId(), dto.channelId());
  }

  public ReadStatus toReadStatusResponse(ReadStatusEntity readStatusEntity) {
    return new ReadStatus(
        readStatusEntity.getId(),
        readStatusEntity.getCreatedAt(),
        readStatusEntity.getUpdatedAt(),
        readStatusEntity.getUserId(),
        readStatusEntity.getChannelId(),
        readStatusEntity.getLastReadAt());
  }
}
