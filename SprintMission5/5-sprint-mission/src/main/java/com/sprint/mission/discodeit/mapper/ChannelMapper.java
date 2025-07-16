package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChannelMapper {


  public ChannelEntity publicChannelCreateDtoToChannel(PublicChannelCreateRequest req) {
    return new ChannelEntity(req.name(), req.description(), ChannelType.PUBLIC);
  }

  public ChannelDto channelToChannelResponseDto(ChannelEntity entity, Instant lastMessageTime,
      List<UUID> userIds) {
    return new ChannelDto(
        entity.getId(),
        entity.getType(),
        entity.getChannelName(),
        entity.getDescription(),
        userIds,
        lastMessageTime
    );
  }

}
