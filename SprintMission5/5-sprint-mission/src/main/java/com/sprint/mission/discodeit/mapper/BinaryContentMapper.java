package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;

import java.io.IOException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BinaryContentMapper {

  public BinaryContentEntity BinaryContentCreateDtoToBinaryContent(BinaryContentCreateDto dto) {
    byte[] fileBytes = null;
    try {
      if (dto.file() != null && !dto.file().isEmpty()) {
        fileBytes = dto.file().getBytes();
      }
    } catch (IOException e) {
      throw new RuntimeException("파일을 읽는 중 오류가 발생했습니다.", e);
    }
    return new BinaryContentEntity(
        dto.userId(),
        dto.messageId(),
        dto.fileName(),
        dto.size(),
        dto.dataType(),
        fileBytes
    );
  }

  public BinaryContent BinaryContentToBinaryContentResponseDto(
      BinaryContentEntity binaryContentEntity) {
    String encodedData = null;
    if (binaryContentEntity.getData() != null) {
      encodedData = Base64.getEncoder().encodeToString(binaryContentEntity.getData());
    }

    return new BinaryContent(
        binaryContentEntity.getId(),
        binaryContentEntity.getCreatedAt(),
        binaryContentEntity.getFileName(),
        binaryContentEntity.getSize(),
        binaryContentEntity.getContentType(),
        encodedData
    );
  }


  public BinaryContentEntity BinaryContentResponseDtoToBinaryContent(BinaryContent dto) {
    byte[] decodedBytes = null;
    if (dto.bytes() != null && !dto.bytes().isEmpty()) {
      decodedBytes = Base64.getDecoder().decode(dto.bytes());
    }
    return new BinaryContentEntity(
        null,
        null,
        dto.fileName(),
        dto.size(),
        dto.contentType(),
        decodedBytes
    );
  }
}
