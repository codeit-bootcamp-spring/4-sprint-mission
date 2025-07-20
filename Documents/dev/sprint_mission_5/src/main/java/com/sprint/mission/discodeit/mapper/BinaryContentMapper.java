package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class BinaryContentMapper {
    public BinaryContent binaryContentCreateDtoToBinaryContent(BinaryContentCreateDto binaryContentCreateDto) {
        try{
        BinaryContent binaryContent = BinaryContent.builder()
                .data(binaryContentCreateDto.getFile().getBytes())
                .fileName(binaryContentCreateDto.getFile().getOriginalFilename())
                .fileType(binaryContentCreateDto.getFileType())
                .createdAt(Instant.now())
                .build();
        return binaryContent;
    } catch (Exception e){
       throw new RuntimeException("Failed to convert MultipartFile to byte array", e);
        }
    }

    public BinaryContentResponseDto binaryContentToBinaryContentResponseDto(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getData()
        );
    }
    public BinaryContent toEntity(MultipartFile file) {
        try{
            return BinaryContent.builder()
                    .id(UUID.randomUUID())
                    .fileName((file.getOriginalFilename()))
                    .fileType(file.getContentType())
                    .data(file.getBytes())
                    .build();
        } catch (IOException e){
            throw new RuntimeException("파일 변환에 실패했습니다.", e);
        }
    }
}
