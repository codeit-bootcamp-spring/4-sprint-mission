package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentCreateDto binaryContentCreateDto);
    BinaryContentResponseDto find(UUID id);
    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> idList);
    void delete(UUID id);
}
