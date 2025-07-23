package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus extends BasedEntity {
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    @Override
    public String toString() {
        return "ReadStatus{" +
                "userId=" + userId +
                ", channelId=" + channelId +
                ", newLastReadAt=" + lastReadAt +
                '}';
    }

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
        super.setActive(ActiveStatus.ACTIVE);
    }
}
