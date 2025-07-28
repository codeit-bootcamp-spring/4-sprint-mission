package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, MessageMapper.class, ReadStatusMapper.class, BinaryContentMapper.class, UserStatusMapper.class})
public interface ChannelMapper {

  default Channel mapToEntity(PublicChannelCreateRequest request) {
    Channel channel = new Channel(
        ChannelType.PUBLIC,
        request.name(),
        request.description()
    );
    return channel;
  }

  Channel mapToPrivateEntity(PrivateChannelCreateRequest request);

  //  @Mapping(source = "newName", target = "name")
//  @Mapping(source = "newDescription", target = "description")
  Channel mapToUpdateEntity(PublicChannelUpdateRequest request);

  ChannelDto mapToDto(Channel channel);

  default ChannelDto mapToDto(Channel channel, List<User> users) {
    List<UUID> participantIds= users.stream()
        .map(User::getId).toList();

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participantIds,
        null
    );
  }

  List<ChannelDto> toDtoList(List<Channel> channels);
}
