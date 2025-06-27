package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ChannelMapper {

    public static ChannelResponseDto channelToChannelResponseDto(Channel channel, List<UUID> participantIds, ReadStatusDto readStatusDto) {
        ChannelResponseDto responseDto = new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                participantIds,
                readStatusDto
        );

        return responseDto;
    }
}
