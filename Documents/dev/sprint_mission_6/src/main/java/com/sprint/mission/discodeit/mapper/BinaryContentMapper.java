package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {


    BinaryContentDto binaryContentToBinaryContentDto(BinaryContent binaryContent);
    default BinaryContent toEntity(MultipartFile file) {
        try {
            BinaryContent binaryContent = new BinaryContent();
            binaryContent.setSize(file.getSize());
            binaryContent.setFileName(file.getOriginalFilename());
            binaryContent.setBytes(file.getBytes());
            binaryContent.setContentType(file.getContentType());
            return binaryContent;
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }
    }

    @Named("binaryContentsToDtos")
    List<BinaryContentDto> binaryContentsToBinaryContentDtos(List<BinaryContent> binaryContents);

    default BinaryContent binaryContentCreateDtoToBinaryContent(BinaryContentCreateDto binaryContentCreateDto) {
        try {
            BinaryContent binaryContent = new BinaryContent();
            binaryContent.setSize(binaryContentCreateDto.getFile().getSize());
            binaryContent.setFileName(binaryContentCreateDto.getFile().getOriginalFilename());
            binaryContent.setBytes(binaryContentCreateDto.getFile().getBytes());
            binaryContent.setContentType(binaryContentCreateDto.getFile().getContentType());
            return binaryContent;
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }
    }
}
