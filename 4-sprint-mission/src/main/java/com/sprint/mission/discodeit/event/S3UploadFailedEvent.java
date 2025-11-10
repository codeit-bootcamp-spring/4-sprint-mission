package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.UUID;

public record S3UploadFailedEvent(
        DiscodeitException exception,
        UUID binaryContentId
) {
}
