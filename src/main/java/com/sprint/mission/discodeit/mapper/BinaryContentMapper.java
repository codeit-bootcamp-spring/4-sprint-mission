package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ContentType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BinaryContentMapper {
    public BinaryContent userCreateDtoToBinaryContent(UserCreateDto dto) {
        return new BinaryContent(
                ContentType.IMAGE.toString(),
                dto.getProfileImage()
        );
    }

    public List<BinaryContent> MessageCreateDtoToBinaryContent(MessageCreateDto messageCreateDto) {
        return messageCreateDto.getFiles().stream()
                .map(bytes -> new BinaryContent(ContentType.FILE.name(), bytes))
                .toList();
    }

    public List<BinaryContent> MessageUpdateDtoToBinaryContent(MessageUpdateDto messageUpdateDto) {
        return messageUpdateDto.getFiles().stream()
                .map(bytes -> new BinaryContent(ContentType.FILE.name(), bytes))
                .toList();
    }

    public BinaryContent binaryContentCreateDtoToBinaryContent(BinaryContentCreateDto binaryContentCreateDto) {
        return new BinaryContent(ContentType.valueOf(binaryContentCreateDto.getContentType()).toString(), binaryContentCreateDto.getContent());
    }

    public BinaryContentResponseDto binaryContentToResponseDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(binaryContent.getId(), binaryContent.getContentType(), binaryContent.getContent());
    }
}
