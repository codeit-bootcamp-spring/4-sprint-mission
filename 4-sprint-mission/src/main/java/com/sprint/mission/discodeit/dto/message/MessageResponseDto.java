package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.MessageState;
import java.util.List;
import java.util.UUID;

public record MessageResponseDto(
        UUID id,
        UUID channelId,
        UUID authorId,
        String content,
        MessageState state,
        List<UUID> attachmentIds) {}
