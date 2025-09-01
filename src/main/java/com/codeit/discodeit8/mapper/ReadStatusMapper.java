package com.codeit.discodeit8.mapper;

import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit8.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  ReadStatusDto toReadStatusDto(ReadStatus readStatus);
}
