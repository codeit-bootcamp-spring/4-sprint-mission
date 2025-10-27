package com.sprint.mission.discodeit.dto.channel.request;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelCreateServiceRequest {

    private final String name;
    private final String description;

    public Channel toEntity(ChannelType channelType) {
        return Channel.builder()
                .name(name)
                .description(description)
                .type(channelType)
                .build();
    }

}
