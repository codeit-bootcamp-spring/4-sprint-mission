package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Component
public class BinaryContentMapper {
    /**
     * BinaryContentCreateDto → BinaryContent 엔티티 변환
     */
    public BinaryContent toEntity(BinaryContentCreateDto dto) throws IOException {
        try {
            return new BinaryContent(
                    dto.getFile().getBytes(),
                    dto.getType()
            );
        } catch (IOException e) {
            throw new UncheckedIOException("멀티파트파일에서 바이트를 읽지 못했습니다.", e);
        }
    }

    /**
     * BinaryContent → BinaryContentResponseDto 변환
     */
    public BinaryContentResponseDto toDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getBytes(),
                binaryContent.getType(),
                binaryContent.getCreatedAt()
        );
    }

    /**
     * BinaryContent → BinaryContentResponseDto를 리스트로 변환
     */
    public List<BinaryContentResponseDto> toDtoList(List<BinaryContent> binaryContents) {
        return binaryContents.stream()
                .map(this::toDto)
                .toList();
    }
}
