package com.sprint.mission.discodeit.dto.channel.request;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrivateChannelCreateServiceRequest {

    private final List<UUID> participantIds;

    public Channel toEntity(ChannelType channelType) {
        return Channel.builder()
                .type(channelType)
                .build();
    }
}
