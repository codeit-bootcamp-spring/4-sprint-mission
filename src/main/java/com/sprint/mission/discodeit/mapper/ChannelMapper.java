package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {
    public Channel toPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        return new Channel(publicChannelCreateRequest.name(), publicChannelCreateRequest.description());
    }

    public Channel toPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        return new Channel(privateChannelCreateRequest.participantIds());
    }

    public ChannelResponseDto ofChannelResponseDto(Channel channel, Instant latestMessageTime) {
        return new ChannelResponseDto(channel.getId(), channel.getChannelName(), channel.getDescription(), channel.getType(), channel.getUserIds(), latestMessageTime);
    }

    public ChannelPublicCreateResponseDto toChannelPublicCreateResponseDto(Channel channel) {
        return new ChannelPublicCreateResponseDto(channel.getId(), channel.getType(), channel.getChannelName(), channel.getDescription());
    }

    public ChannelPrivateCreateResponseDto toChannelPrivateCreateResponseDto(Channel channel, List<UUID> ids) {
        return new ChannelPrivateCreateResponseDto(channel.getId(), channel.getType(), ids);
    }

    public ChannelDto ofChannelDto(Channel channel, Instant latestMessageTime, List<UUID> ids) {
        return new ChannelDto(channel.getId(), channel.getType(), channel.getChannelName(), channel.getDescription(), ids, latestMessageTime);
    }
}
