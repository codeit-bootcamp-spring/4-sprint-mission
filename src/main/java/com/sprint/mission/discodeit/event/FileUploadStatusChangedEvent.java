package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record FileUploadStatusChangedEvent(
        UUID binaryContentId
) {
}
