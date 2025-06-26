package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import java.util.List;
import java.util.UUID;

public record MessageCreateDto(
        String content, List<BinaryContentRequestDto> binaryContents, UUID userId, UUID channelId) {}
