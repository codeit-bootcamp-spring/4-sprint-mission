package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {

    public Channel createPublicChannelDtoToChannel(PublicChannelCreateDto dto) {
        return new Channel(ChannelType.PUBLIC, dto.getName(), dto.getDescription());
    }

    public Channel createPrivateChannelDtoToChannel(PrivateChannelCreateDto dto) {
        return new Channel(ChannelType.PRIVATE, null, null);
    }

    public ChannelResponseDto channelToChannelResponseDto(Channel channel, List<UUID> participantIds) {
        ChannelResponseDto responseDto = new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                participantIds
        );

        return responseDto;
    }
}
