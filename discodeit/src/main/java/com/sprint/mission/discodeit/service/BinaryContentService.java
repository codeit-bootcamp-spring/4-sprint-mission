package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentResponseDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentCreateDto dto);
    BinaryContentResponseDto find(UUID id);
    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);
}
