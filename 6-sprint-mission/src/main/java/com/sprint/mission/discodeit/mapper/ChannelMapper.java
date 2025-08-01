package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ChannelType.class, uses = UserMapper.class)
public interface ChannelMapper {

    @Mapping(target = "type", expression = "java(ChannelType.PUBLIC)")
    Channel publicChannelCreateDtoToChannel(PublicChannelCreateRequest req);

    @Mapping(target = "participants", source = "userDtoList")
    @Mapping(target = "lastMessageAt", source = "lastMessageTime")
    ChannelDto channelToChannelDto(Channel entity, Instant lastMessageTime, List<UserDto> userDtoList);
}
