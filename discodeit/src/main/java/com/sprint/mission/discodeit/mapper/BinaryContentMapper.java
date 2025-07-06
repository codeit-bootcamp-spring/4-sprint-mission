package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BinaryContentMapper {
    public BinaryContent binaryContentCreateDtoToBinaryContent(BinaryContentCreateDto binaryContentDto) {
        try {
            return new BinaryContent(
                    null,
                    null,
                    binaryContentDto.getFile().getBytes(),
                    binaryContentDto.getFile().getOriginalFilename(),
                    binaryContentDto.getFile().getContentType()
            );
        } catch (IOException e) {
         throw new RuntimeException("파일 데이터를 읽는 중 오류가 발생했습니다.",e);
        }
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
