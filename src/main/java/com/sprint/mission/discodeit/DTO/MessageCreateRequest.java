package com.sprint.mission.discodeit.DTO;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<byte[]> attachments
) {}
