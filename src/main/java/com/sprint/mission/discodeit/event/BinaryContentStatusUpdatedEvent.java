package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;

import java.util.UUID;

public record BinaryContentStatusUpdatedEvent(UUID binaryContentId, BinaryContentStatus newStatus) {
}
