package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.status.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    BinaryContentStatus status,
    UUID ownerId //소유자 ID
) {

}
