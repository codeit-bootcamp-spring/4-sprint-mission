package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponseDto (
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UserDto author,
        List<BinaryContent> attachments
){
}
