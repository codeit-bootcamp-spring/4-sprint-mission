package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {

    public Channel channelPublicCreateDtoToChannel(ChannelPublicCreateDto dto) {
        return new Channel(
                dto.channelType(),
                dto.channelName(),
                dto.description()
        );
    }

    public Channel channelPrivateCreateDtoToChannel(ChannelPrivateCreateDto dto) {
        return new Channel(
                dto.channelType(),
                "private",
                "private"
        );
    }

    public ChannelResponseDto channelToChannelResponseDto(Channel channel, Instant lastMessageTime, List<UUID> participantUserIds) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getChannelType(),
                channel.getChannelName(),
                channel.getDescription(),
                lastMessageTime,
                participantUserIds
        );
    }
}
