package com.sprint.mission.discodeit.dto.binarycontent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContent(
    UUID id,
    Instant createdAt,
    String fileName,
    long size,
    String contentType,
    String bytes) {

}
