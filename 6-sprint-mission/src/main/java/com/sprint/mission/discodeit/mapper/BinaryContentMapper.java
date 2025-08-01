package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  @Mapping(target = "bytes", source = "encodedData")
  BinaryContentDto binaryContentToBinaryContentDto(BinaryContent binaryContent, String encodedData);

  BinaryContent binaryContentDtoToBinaryContent(BinaryContentDto dto);

  BinaryContent binaryContentCreateServiceRequestToBinaryContent(BinaryContentCreateServiceRequest request);

  @Named("binaryToDto")
  @Mapping(target = "bytes", source = "encodedBytes")
  BinaryContentDto binaryContentToDtoWithBytes(BinaryContent entity, String encodedBytes);

  @Named("binaryToDtoList")
  List<BinaryContentDto>  binaryContentToBinaryContentDtoList(List<BinaryContent> entityList);
}
