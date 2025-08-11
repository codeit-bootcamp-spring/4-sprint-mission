package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  ReadStatusDto toReadStatusDto(ReadStatus readStatus);
}
