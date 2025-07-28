package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  ReadStatus mapToCreateEntity(ReadStatusCreateRequest request, User user, Channel channel);

//  @Mapping(source = "newLastReadAt", target = "lastReadAt")
//  ReadStatus mapToUpdateEntity(ReadStatusUpdateRequest request, User user, Channel channel);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  ReadStatusDto mapToDto(ReadStatus readStatus);

  List<ReadStatusDto> mapToDtoList(List<ReadStatus> readStatuses);
}
