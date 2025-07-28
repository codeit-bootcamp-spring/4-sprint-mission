package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  Message toCreateEntity(MessageCreateRequest request);

//  @Mapping(source = "newContent", target = "content")
  Message toUpdateEntity(MessageUpdateRequest request);

  MessageDto toDto(Message Message);

  List<MessageDto> toDtoList(List<Message> messages);
}
