package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;

import java.util.List;
import java.util.UUID;

public record MessageUpdateDto(
        UUID id,
        UUID authorId,
        String newContent,
        List<BinaryContentCreateDto> nweBinaryContentCreateDtos
) {}
