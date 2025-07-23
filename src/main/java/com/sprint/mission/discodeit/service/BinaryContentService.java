package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentPostDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto create(BinaryContentPostDto binaryContentPostDto);

    BinaryContentResponseDto find(UUID id);

    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);

    List<BinaryContentResponseDto> findAll();
}
