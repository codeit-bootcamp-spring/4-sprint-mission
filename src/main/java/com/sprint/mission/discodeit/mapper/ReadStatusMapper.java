package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {
    ReadStatusMapper INSTANCE = Mappers.getMapper(ReadStatusMapper.class);

//    @Mapping(source = "userId", target = "user.id")
//    @Mapping(source = "channelId", target = "channel.id")
//    ReadStatus toEntity(ReadStatusDto readStatusDto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "channel.id",target = "channelId")
    ReadStatusDto toDto(ReadStatus readStatus);
}
