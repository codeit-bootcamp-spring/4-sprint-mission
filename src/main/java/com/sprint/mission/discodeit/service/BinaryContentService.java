package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.CreateBinaryContentRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto createBinaryContents(CreateBinaryContentRequestDto createBinaryContentRequestDto);
    List<BinaryContentResponseDto> findAllBinaryContentDtos();
    BinaryContentResponseDto findBinaryContentDtoByBinaryContentId(UUID binaryContentId);
    void deleteBinaryContentDTOById(UUID binaryContentId);

    List<BinaryContentResponseDto> findBinaryContentDtosByReferenceId(UUID referenceId);
    BinaryContent findBinaryContentByBinaryContentId(UUID binaryContentId);
}
