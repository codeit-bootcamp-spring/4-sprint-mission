package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentsResponseDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.CreateBinaryContentsRequestDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentsService {
    BinaryContentsResponseDto createBinaryContents(CreateBinaryContentsRequestDto createBinaryContentsRequestDto);
    List<BinaryContentsResponseDto> findAllBinaryContentsDtos();
    BinaryContentsResponseDto findBinaryContentsDTOByBinaryContentsId(UUID binaryContentId);
    void deleteBinaryContentsDTOById(UUID binaryContentId);

    List<BinaryContentsResponseDto> findBinaryContentsDtosByReferenceId(UUID referenceId);
}
