package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto.*;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {

  public ChannelResponse toChannelResponse(Channel channel) {
    return new ChannelResponse(
        channel.getId(),
        channel.getName(),
        channel.getDescription(),
        channel.getCreatedAt(),
        channel.getUpdatedAt(),
        channel.getType()
    );
  }

  public UserChannelResponse toUserChannelResponse(Channel c, List<UUID> userIds,
      Instant lastMessage) {
    return new UserChannelResponse(
        c.getId(),
        c.getName(),
        c.getDescription(),
        c.getType(),
        lastMessage,
        userIds
    );
  }
}
