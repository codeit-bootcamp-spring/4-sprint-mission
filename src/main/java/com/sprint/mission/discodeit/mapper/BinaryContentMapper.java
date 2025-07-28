package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

    BinaryContentMapper INSTANCE = Mappers.getMapper(BinaryContentMapper.class);

    BinaryContentDto toDto(com.sprint.mission.discodeit.entity.BinaryContent binaryContent);

    default BinaryContent toEntity(BinaryContentCreateRequest request) {
        return new BinaryContent(
                request.fileName(),
                (long)request.bytes().length,
                request.contentType(),
                request.bytes()
        );
    }
}
