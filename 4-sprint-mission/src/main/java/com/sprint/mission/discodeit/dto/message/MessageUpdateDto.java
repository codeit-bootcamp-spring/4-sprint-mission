package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import java.util.List;
import java.util.UUID;

public record MessageUpdateDto(
        UUID id,
        String content,
        List<UUID> removeAttachments,
        List<BinaryContentRequestDto> newAttachments) {}
