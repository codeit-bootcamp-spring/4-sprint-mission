package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDownloadResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "size", expression = "java((long) request.bytes().length)")
    BinaryContent toEntity(BinaryContentCreateRequest request);

    BinaryContentDto toDto(BinaryContent entity);

    @Mapping(target = "data", source = "bytes")
    BinaryContentDownloadResponse toDownloadResponse(BinaryContent entity);
}
