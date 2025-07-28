package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String Content,
    UUID channelId,
    UserDto author,
    List<BinaryContentDto> attachments
) {

}
