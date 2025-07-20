package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {

    // 1. PublicChannelCreateRequest → Channel (Entity)
    public Channel toEntity(PublicChannelCreateRequest dto) {
        return new Channel(ChannelType.PUBLIC, dto.name(), dto.description());
    }

    // 2. PrivateChannelCreateRequest → Channel (Entity)
    public Channel toEntity(PrivateChannelCreateRequest dto) {
        return new Channel(ChannelType.PRIVATE, null, null); // 이름/설명 없음
    }

    // 3. Channel → ChannelResponse
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

    public UserChannelResponse toUserChannelResponse(Channel channel, List<UUID> participantIds, Instant lastMessageAt) {
        return new UserChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType(),
                lastMessageAt,
                participantIds
        );
    }

}
