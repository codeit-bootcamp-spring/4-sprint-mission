package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChannelMapper {

    public Channel channelCreateDtoToChannel(ChannelCreateDto channelCreateDto) {
        return new Channel(
                channelCreateDto.name(), channelCreateDto.description(), channelCreateDto.channelType());
    }

    public ChannelResponseDto channelToChannelResponseDto(
            Channel channel, Instant lastMessageTime, List<UUID> userIds) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getChannelName(),
                channel.getDescription(),
                channel.getType(),
                lastMessageTime,
                userIds);
    }
}
