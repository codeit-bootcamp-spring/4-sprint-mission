package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "channel.id", target = "channelId")
    ReadStatusDto toDto(ReadStatus readStatus);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)        //외부에서 주입
    @Mapping(target = "channel", ignore = true)     //외부에서 주입
    ReadStatus toEntity(ReadStatusCreateRequest request);

    @Mapping(source = "newLastReadAt", target = "lastReadAt")
    void updateFromRequest(ReadStatusUpdateRequest request, @MappingTarget ReadStatus entity);
}
