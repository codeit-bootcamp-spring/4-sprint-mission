package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BinaryContentMapper {

    // DTO -> Entity
    public BinaryContent toEntity(BinaryContentCreateDto dto, UUID userId) {
        return new BinaryContent(
                userId,
                null,
                dto.getBytes(),
                dto.getFileName(),
                dto.getFileType()
        );
    }
}
