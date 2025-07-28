package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  @Mapping(source = "fileName", target = "fileName")
  @Mapping(source = "size", target = "size")
  @Mapping(source = "contentType", target = "contentType")
  @Mapping(source = "bytes", target = "bytes")
  BinaryContent mapToEntity(BinaryContentCreateRequest request);

  BinaryContentDto mapToDto(BinaryContent binaryContent);

  List<BinaryContentDto> mapToDtoList(List<BinaryContent> binaryContents);
}
