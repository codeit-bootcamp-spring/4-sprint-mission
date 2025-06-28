package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {
    public BinaryContent binaryContentCreateDtoToBinaryContent (BinaryContentCreateDto dto) {
        return new BinaryContent(
                dto.userId(),
                dto.messageId(),
                dto.binaryContentType(),
                dto.bytes(),
                dto.fileName(),
                dto.fileType(),
                dto.fileSize()
        );
    }

    public BinaryContentResponseDto binaryContentToBinaryContentResponseDto (BinaryContent binaryContent) {
        return new BinaryContentResponseDto (
                binaryContent.getId(),
                binaryContent.getBytes(),
                binaryContent.getFileType(),
                binaryContent.getFileName(),
                binaryContent.getFileSize()
        );
    }
}
