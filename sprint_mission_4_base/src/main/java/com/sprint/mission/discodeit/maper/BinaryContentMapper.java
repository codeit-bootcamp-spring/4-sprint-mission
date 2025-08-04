package com.sprint.mission.discodeit.maper;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BinaryContentMapper {
    public BinaryContent binaryContentCreateRequestToBinaryContent(BinaryContentCreateRequest dto) {
        String fileName = dto.fileName();
        String contentType = dto.contentType();

        byte[] bytes = null;

        try {
            bytes = dto.bytes().getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
    }

    public BinaryContentResponseDto binaryContentToResponseDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType(),
                binaryContent.getBytes()
                );
    }
}
