package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {
    public Channel publicChannelCreateDtoToChannel(PublicChannelCreateDto dto) {
        return new Channel(
                dto.getName(),
                dto.getDescription(),
                ChannelType.PUBLIC
        );
    }

    public Channel privateChannelCreateDtoToChannel(PrivateChannelCreateDto dto) {
        return new Channel(
                dto.getMembers(),
                ChannelType.PRIVATE
        );
    }

    public ChannelResponseDto channelToChannelResponseDto(Channel channel) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.getRecentMessageTime(),
                channel.getMembers()
        );
    }

    public ChannelResponseDto channelToChannelResponseDto(Channel channel, List<UUID> members) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.getRecentMessageTime(),
                channel.getMembers()
        );
    }
}
