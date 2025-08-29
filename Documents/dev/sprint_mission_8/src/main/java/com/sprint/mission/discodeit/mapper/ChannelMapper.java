package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

    Channel publicChannelCreateDtoToChannel(PublicChannelCreateDto dto);
    Channel privateChannelCreateDtoToChannel(PrivateChannelCreateDto dto);
    ChannelDto channelToChannelDto(Channel channel, List<UserDto> participants);

    @Mapping(target = "participants", ignore = true)
    ChannelDto channelToChannelDto(Channel channel);

//    public Channel publicChannelCreateDtoToChannel(PublicChannelCreateDto dto) {
//        return new Channel(
//                dto.getChannelName(),
//                dto.getDescription(),
//                ChannelType.PUBLIC
//        );
//    }

//    public Channel privateChannelCreateDtoToChannel(PrivateChannelCreateDto dto) {
//        return new Channel(
//                dto.getMembers(),
//                ChannelType.PRIVATE
//        );
//    }

//    public ChannelResponseDto channelToChannelResponseDto(Channel channel) {
//        return new ChannelResponseDto(
//                channel.getId(),
//                channel.getType(),
//                channel.getChannelName(),
//                channel.getDescription(),
//                channel.getRecentMessageTime(),
//                channel.getMembers()
//        );
//    }

//    public ChannelDto channelToChannelResponseDto(Channel channel, List<UUID> members) {
//        return new ChannelDto(
//                channel.getId(),
//                channel.getType(),
//                channel.getChannelName(),
//                channel.getDescription(),
//                channel.getRecentMessageTime(),
//                channel.getMembers()
//        );
//    }
}
