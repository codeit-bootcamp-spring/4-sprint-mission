package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;

@Mapper(
        uses = {UserMapper.class}
)
public interface BinaryContentMapper {

    BinaryContentResponse toResponse(BinaryContent binaryContent);
}
