package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.FileAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
                    dto.getFile().getOriginalFilename(),
                    dto.getFile().getContentType(),
                    dto.getFile().getSize(),
                    dto.getType()
            );
        } catch (IOException e) {
            // 트랜잭션시 롤백을 고려해서 RuntimeException을 상속받은 FileAccessException 형태로 예외 전환해서 던지도록 설정했습니다.
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        }
    }

    /**
     * MultipartFile + BinaryContentType → BinaryContent 엔티티 변환
     */
    public BinaryContent toEntity(MultipartFile file, BinaryContentType type) {
        try {
            return new BinaryContent(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    type
            );
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        }
    }

    /**
     * List<MultipartFile> + BinaryContentType → List<BinaryContent> 변환
     */
    public List<BinaryContent> toEntities(
            List<MultipartFile> files,
            BinaryContentType type
    ) {
        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> toEntity(file, type))
                .toList();
    }

    /**
     * BinaryContent → BinaryContentResponseDto 변환
     */
    public BinaryContentResponseDto toDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getBytes(),
                binaryContent.getType(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getSize(),
                binaryContent.getCreatedAt()
        );
    }

    /**
     * BinaryContent → BinaryContentResponseDto를 리스트로 변환
     */
    public List<BinaryContentResponseDto> toDtos(List<BinaryContent> binaryContents) {
        return binaryContents.stream()
                .map(this::toDto)
                .toList();
    }
}
