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
    public ChannelMapper() {
    }

    public Channel toEntity(PublicChannelCreateDto dto) {
        return new Channel(
                ChannelType.PUBLIC,
                dto.getName(),
                dto.getDescription()
        );
    }

    public ChannelResponseDto toDto(Channel channel, Instant lastMessageAt, List<UUID> usersId) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                usersId
        );
    }
}
