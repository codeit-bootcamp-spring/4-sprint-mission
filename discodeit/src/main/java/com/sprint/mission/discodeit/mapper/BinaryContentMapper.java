package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
    public BinaryContent binaryContentCreateDtoToBinaryContent(BinaryContentDto binaryContentDto) {
        return new BinaryContent(
                null,
                null,
                binaryContentDto.getData(),
                binaryContentDto.getFileName(),
                binaryContentDto.getFileType()
        );
    }

    public BinaryContentResponseDto entityToDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getUserId(),
                binaryContent.getMessageId(),
                binaryContent.getDatas(),
                binaryContent.getFilename(),
                binaryContent.getFileType()
        );
    }
}
