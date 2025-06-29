package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {
    public Channel publicDtoToChannel(PublicChannelCreateDto dto) {
        return new Channel(ChannelType.PUBLIC,dto.getName(), dto.getDescription());
    }

    public Channel privateDtoToChannel() {
        return new Channel(ChannelType.PRIVATE,null, null);
    }

    public ChannelResponseDto channelToResponseDto(Channel channel, Instant lastMessageTime, List<UUID> participantIds) {
        return new ChannelResponseDto(channel.getId(), channel.getType(), channel.getName(), channel.getDescription(), lastMessageTime, participantIds);
    }

    public List<ChannelResponseDto> channelsToResponseDto(List<Channel> channels) {
        return channels.stream()
                .map(channel -> new ChannelResponseDto(channel.getId(), channel.getType(), channel.getName(), channel.getDescription(), null, null))
                .toList();
    }
}
