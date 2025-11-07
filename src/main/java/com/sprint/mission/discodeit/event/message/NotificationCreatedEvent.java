package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.NotificationDto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class NotificationCreatedEvent extends CreatedEvent<NotificationDto> {
    private final Set<UUID> receiverIds;
    public NotificationCreatedEvent(NotificationDto data, Set<UUID> receiverIds, Instant createdAt) {
        super(data, createdAt);
        this.receiverIds = receiverIds;
    }
    public Set<UUID> getReceiverIds() { return receiverIds; }
}
