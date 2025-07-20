package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelRequestDto(
    ChannelType type,
    String name,
    String description
) {
   public static ChannelRequestDto create(ChannelType type, String name, String description) {
       return new ChannelRequestDto(type, name, description);
   }
}
