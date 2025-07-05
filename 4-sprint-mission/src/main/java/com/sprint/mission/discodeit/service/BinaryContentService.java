package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    public BinaryContentResponseDto createBinaryContent(
            BinaryContentCreateDto binaryContentCreateDto);

    public BinaryContentResponseDto findBinaryContentById(UUID id);

    public List<BinaryContentResponseDto> findAllBinaryContentById(UUID id);

    public List<BinaryContentResponseDto> findAllBinaryContentByIdIn(List<UUID> ids);

    public void deleteBinaryContent(UUID id);
}
