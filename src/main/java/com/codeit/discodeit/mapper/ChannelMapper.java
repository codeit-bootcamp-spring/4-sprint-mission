package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.channel_service_dto.ChannelDto;
import com.codeit.discodeit.dto.channel_service_dto.CreatePublicChannelRequestDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChannelMapper {

  @Mapping(target = "type", constant = "PUBLIC")
  Channel toPublicChannel(CreatePublicChannelRequestDto dto);

  default Channel toPrivateChannel() {
    Channel channel = new Channel();
    channel.setType(ChannelType.PRIVATE);
    return channel;
  }

  default ChannelDto toChannelDto(Channel channel, Optional<Message> lastMessage,
      List<ReadStatus> readStatuses, @Context UserMapper userMapper) {
    ChannelDto dto = new ChannelDto();
    dto.setId(channel.getId());
    dto.setName(channel.getName());
    dto.setDescription(channel.getDescription());
    dto.setType(channel.getType());

    dto.setLastMessageAt(lastMessage.map(Message::getCreatedAt).orElse(null));
    dto.setParticipants(readStatuses.stream()
        .map(rs -> userMapper.toUserDto(rs.getUser()))
        .toList());

    return dto;
  }
}