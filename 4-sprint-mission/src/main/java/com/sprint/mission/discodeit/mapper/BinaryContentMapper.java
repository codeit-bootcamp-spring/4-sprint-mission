package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BinaryContentMapper {
    public BinaryContent BinaryContentRequestDtoToBinaryContent(
            BinaryContentRequestDto dto, UUID ownerId) {
        return new BinaryContent(ownerId, null, dto.data(), dto.dataType());
    }

    public BinaryContent BinaryContentCreateDtoToBinaryContent(BinaryContentCreateDto dto) {
        return new BinaryContent(dto.userId(), dto.messageId(), dto.data(), dto.dataType());
    }

    public BinaryContentResponseDto BinaryContentToBinaryContentResponseDto(
            BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getUserId(),
                binaryContent.getMessageId(),
                Base64.getEncoder().encodeToString(binaryContent.getData()),
                binaryContent.getDataType());
    }

    public BinaryContent BinaryContentResponseDtoToBinaryContent(BinaryContentResponseDto dto) {
        byte[] decodedBytes = Base64.getDecoder().decode(dto.data());
        return new BinaryContent(dto.userId(), dto.messageId(), decodedBytes, dto.dataType());
    }
}
