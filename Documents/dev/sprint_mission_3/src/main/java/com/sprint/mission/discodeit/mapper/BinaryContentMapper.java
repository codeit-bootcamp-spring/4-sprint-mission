package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
    public BinaryContent binaryContentCreateDtoToBinaryContent(BinaryContentCreateDto binaryContentCreateDto) {
        BinaryContent binaryContent = new BinaryContent(
                null,
                null,
                null,
                binaryContentCreateDto.getBytes(),
                binaryContentCreateDto.getFileName(),
                binaryContentCreateDto.getFileType()
        );
        return binaryContent;
    }

    public BinaryContentResponseDto binaryContentToBinaryContentResponseDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getData()
        );
    }
}
