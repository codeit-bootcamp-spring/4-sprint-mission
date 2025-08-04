package com.sprint.mission.discodeit.maper;

import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {
    public Channel publicCreateRequestToChannel(PublicChannelCreateRequest dto) {
        return new Channel(ChannelType.PUBLIC, dto.name(), dto.description());
    }

    public Channel privateCreateRequestToChannel() {
        return new Channel(ChannelType.PRIVATE, null, null);
    }

    public ChannelResponseDto channelToResponseDto(Channel channel, List<UUID> participantIds,  Instant lastMessageAt) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }

}
