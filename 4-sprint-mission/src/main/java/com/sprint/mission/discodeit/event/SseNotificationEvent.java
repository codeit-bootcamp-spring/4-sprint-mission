package com.sprint.mission.discodeit.event;

import java.util.List;
import java.util.UUID;

public record SseNotificationEvent(
        List<UUID> receiverIds,
        String name,
        Object data
) {
}
